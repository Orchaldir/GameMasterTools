package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.core.selector.getRaceAppearance
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.visualizeCharacter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureAppearanceRouting() {
    routing {
        get<Characters.Appearance.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, character)
            }
        }
        post<Characters.Appearance.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(preview.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val appearance = parseAppearance(formParameters, config, character)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, updatedCharacter)
            }
        }
        post<Characters.Appearance.Update> { update ->
            logger.info { "Update character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(update.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val appearance = parseAppearance(formParameters, config, character)

            STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<Characters.Appearance.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(update.id)
            val config = createGenerationConfig(state, character)
            val appearance = generateAppearance(config, character)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, updatedCharacter)
            }
        }
    }
}

private fun HTML.showAppearanceEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val appearance = character.appearance
    val raceAppearance = state.getRaceAppearance(character)
    val culture = state.getCultureStorage().getOrThrow(character.culture)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Appearance.Preview(character.id))
    val updateLink = call.application.href(Characters.Appearance.Update(character.id))
    val generateLink = call.application.href(Characters.Appearance.Generate(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, state, character)
    val backSvg = visualizeCharacter(RENDER_CONFIG, state, character, renderFront = false)

    simpleHtml("Edit Appearance: ${state.getName(character)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            p {
                submitInput {
                    value = "Random"
                    formAction = generateLink
                    formMethod = InputFormMethod.post
                }
            }
            selectOneOf("Appearance Type", APPEARANCE, raceAppearance.appearanceTypes, true) { type ->
                label = type.name
                value = type.toString()
                selected = when (type) {
                    AppearanceType.Body -> appearance is HumanoidBody
                    AppearanceType.HeadOnly -> appearance is HeadOnly
                }
            }
            when (appearance) {
                is HeadOnly -> {
                    editHeight(state, character, appearance.height)
                    editHead(raceAppearance, culture, appearance.head)
                    editSkin(raceAppearance, appearance.head.skin)
                }

                is HumanoidBody -> {
                    editHeight(state, character, appearance.height)
                    editBody(character, appearance.body)
                    editHead(raceAppearance, culture, appearance.head)
                    editSkin(raceAppearance, appearance.head.skin)
                }

                UndefinedAppearance -> doNothing()
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}

private fun FORM.editHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)
    field("Max Height") {
        selectFloat(maxHeight.value, race.height.getMin(), race.height.getMax(), 0.01f, HEIGHT)
        +" m"
    }
    showCurrentHeight(state, character, maxHeight)
}

private fun FORM.editBody(
    character: Character,
    body: Body,
) {
    h2 { +"Body" }
    selectEnum("Shape", BODY_SHAPE, getAvailableBodyShapes(character.gender), true) { shape ->
        label = shape.name
        value = shape.toString()
        selected = body.bodyShape == shape
    }
    selectEnum("Width", BODY_WIDTH, Size.entries, true) { width ->
        label = width.name
        value = width.toString()
        selected = body.width == width
    }
}

private fun FORM.editHead(
    raceAppearance: RaceAppearance,
    culture: Culture,
    head: Head,
) {
    editEars(raceAppearance, head.ears)
    editEyes(raceAppearance, head.eyes)
    editHair(raceAppearance, culture, head.hair)
    editMouth(raceAppearance, culture, head.mouth)
}

private fun FORM.editEars(raceAppearance: RaceAppearance, ears: Ears) {
    h2 { +"Ears" }
    selectOneOf("Type", EAR_TYPE, raceAppearance.earsLayout, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            EarsLayout.NoEars -> ears is NoEars
            EarsLayout.NormalEars -> ears is NormalEars
        }
    }
    when (ears) {
        is NormalEars -> {
            selectOneOf("Ear Shape", EAR_SHAPE, raceAppearance.earShapes, true) { shape ->
                label = shape.name
                value = shape.toString()
                selected = ears.shape == shape
            }
            selectEnum("Ear Size", EAR_SIZE, Size.entries, true) { size ->
                label = size.name
                value = size.toString()
                selected = ears.size == size
            }
        }

        else -> doNothing()
    }
}

private fun FORM.editSkin(
    raceAppearance: RaceAppearance,
    skin: Skin,
) {
    h2 { +"Skin" }
    selectOneOf("Type", SKIN_TYPE, raceAppearance.skinTypes, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            SkinType.Scales -> skin is Scales
            SkinType.Normal -> skin is NormalSkin
            SkinType.Exotic -> skin is ExoticSkin
        }
    }
    when (skin) {
        is Scales -> selectColor("Color", SKIN_EXOTIC_COLOR, raceAppearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", SKIN_EXOTIC_COLOR, raceAppearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectOneOf("Color", SKIN_COLOR, raceAppearance.normalSkinColors, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                selected = skin.color == skinColor
                val bgColor = RENDER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

private fun FORM.editBeard(
    raceAppearance: RaceAppearance,
    culture: Culture,
    beard: Beard,
) {
    h2 { +"Beard" }
    selectOneOf("Type", BEARD, raceAppearance.hairOptions.beardTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            BeardType.None -> beard is NoBeard
            BeardType.Normal -> beard is NormalBeard
        }
    }
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> editNormalBeard(raceAppearance, culture, beard)
    }
}

private fun FORM.editNormalBeard(
    raceAppearance: RaceAppearance,
    culture: Culture,
    beard: NormalBeard,
) {
    selectOneOf("Style", combine(BEARD, STYLE), culture.appearanceStyle.beardStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = when (style) {
            BeardStyleType.Goatee -> beard.style is Goatee
            BeardStyleType.GoateeAndMoustache -> beard.style is GoateeAndMoustache
            BeardStyleType.Moustache -> beard.style is Moustache
            BeardStyleType.Shaved -> beard.style is ShavedBeard
        }
    }
    selectColor("Color", combine(BEARD, COLOR), raceAppearance.hairOptions.colors, beard.color)

    when (beard.style) {
        is Goatee -> selectGoateeStyle(culture, beard.style.goateeStyle)
        is GoateeAndMoustache -> {
            selectGoateeStyle(culture, beard.style.goateeStyle)
            selectMoustacheStyle(culture, beard.style.moustacheStyle)
        }

        is Moustache -> selectMoustacheStyle(culture, beard.style.moustacheStyle)
        ShavedBeard -> doNothing()
    }
}

private fun HtmlBlockTag.selectGoateeStyle(
    culture: Culture,
    goateeStyle: GoateeStyle,
) {
    selectOneOf("Goatee", GOATEE_STYLE, culture.appearanceStyle.goateeStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = style == goateeStyle
    }
}

private fun HtmlBlockTag.selectMoustacheStyle(
    culture: Culture,
    moustacheStyle: MoustacheStyle,
) {
    selectOneOf("Moustache", MOUSTACHE_STYLE, culture.appearanceStyle.moustacheStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = style == moustacheStyle
    }
}

private fun FORM.editEyes(
    raceAppearance: RaceAppearance,
    eyes: Eyes,
) {
    h2 { +"Eyes" }
    selectOneOf("Layout", EYES_LAYOUT, raceAppearance.eyesLayout, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            EyesLayout.NoEyes -> eyes is NoEyes
            EyesLayout.OneEye -> eyes is OneEye
            EyesLayout.TwoEyes -> eyes is TwoEyes
        }
    }
    when (eyes) {
        is OneEye -> {
            editEye(raceAppearance.eyeOptions, eyes.eye)
            selectEnum("Eye Size", EYE_SIZE, Size.entries, true) { c ->
                label = c.name
                value = c.toString()
                selected = eyes.size == c
            }
        }

        is TwoEyes -> {
            editEye(raceAppearance.eyeOptions, eyes.eye)
        }

        else -> doNothing()
    }
}

private fun FORM.editEye(
    eyeOptions: EyeOptions,
    eye: Eye,
) {
    selectOneOf("Eye Shape", EYE_SHAPE, eyeOptions.eyeShapes, true) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.eyeShape == shape
    }
    selectOneOf("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes, true) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.pupilShape == shape
    }
    selectColor("Pupil Color", PUPIL_COLOR, eyeOptions.pupilColors, eye.pupilColor)
    selectColor("Sclera Color", SCLERA_COLOR, eyeOptions.scleraColors, eye.scleraColor)
}

private fun FORM.editHair(
    raceAppearance: RaceAppearance,
    culture: Culture,
    hair: Hair,
) {
    h2 { +"Hair" }
    selectOneOf("Type", HAIR_TYPE, raceAppearance.hairOptions.hairTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            HairType.None -> hair is NoHair
            HairType.Normal -> hair is NormalHair
        }
    }
    when (hair) {
        NoHair -> doNothing()
        is NormalHair -> editNormalHair(raceAppearance, culture, hair)
    }
}

private fun FORM.editNormalHair(
    raceAppearance: RaceAppearance,
    culture: Culture,
    hair: NormalHair,
) {
    selectOneOf("Style", HAIR_STYLE, culture.appearanceStyle.hairStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = when (style) {
            HairStyleType.BuzzCut -> hair.style is BuzzCut
            HairStyleType.FlatTop -> hair.style is FlatTop
            HairStyleType.MiddlePart -> hair.style is MiddlePart
            HairStyleType.Shaved -> hair.style is ShavedHair
            HairStyleType.SidePart -> hair.style is SidePart
            HairStyleType.Spiked -> hair.style is Spiked
        }
    }
    selectColor("Color", HAIR_COLOR, raceAppearance.hairOptions.colors, hair.color)

    when (hair.style) {
        is SidePart -> {
            selectEnum("Side", SIDE_PART, Side.entries, true) { side ->
                label = side.name
                value = side.toString()
                selected = hair.style.side == side
            }
        }

        else -> doNothing()
    }
}

private fun FORM.editMouth(
    raceAppearance: RaceAppearance,
    culture: Culture,
    mouth: Mouth,
) {
    h2 { +"Mouth" }
    selectOneOf("Type", MOUTH_TYPE, raceAppearance.mouthTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            MouthType.NoMouth -> mouth is NoMouth
            MouthType.NormalMouth -> mouth is NormalMouth || mouth is FemaleMouth
        }
    }
    when (mouth) {
        is NormalMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            editBeard(raceAppearance, culture, mouth.beard)
        }

        is FemaleMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            selectColor("Lip Color", LIP_COLOR, culture.appearanceStyle.lipColors, mouth.color)
        }

        else -> doNothing()
    }
}

private fun FORM.editSimpleMouth(size: Size, teethColor: TeethColor) {
    selectEnum("Width", MOUTH_WIDTH, Size.entries, true) { width ->
        label = width.name
        value = width.toString()
        selected = size == width
    }
    selectEnum("Teeth Color", TEETH_COLOR, TeethColor.entries, true) { color ->
        label = color.name
        value = color.toString()
        selected = teethColor == color
    }
}
