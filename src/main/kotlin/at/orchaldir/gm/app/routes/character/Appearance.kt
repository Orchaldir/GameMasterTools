package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.createGenerationConfig
import at.orchaldir.gm.app.parse.generateAppearance
import at.orchaldir.gm.app.parse.parseAppearance
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.SidePart
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.FootOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.WingOptions
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.getRaceAppearance
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
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
        get<CharacterRoutes.Appearance.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Appearance.Preview> { preview ->
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
        post<CharacterRoutes.Appearance.Update> { update ->
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
        post<CharacterRoutes.Appearance.Generate> { update ->
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
    val previewLink = call.application.href(CharacterRoutes.Appearance.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Appearance.Update(character.id))
    val generateLink = call.application.href(CharacterRoutes.Appearance.Generate(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, renderFront = false)

    simpleHtml("Edit Appearance: ${character.name(state)}", true) {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                button("Random", generateLink)
                selectOneOf(
                    "Appearance Type",
                    APPEARANCE,
                    raceAppearance.appearanceTypes,
                    appearance.getType(),
                    true
                ) { type ->
                    label = type.name
                    value = type.toString()
                }
                when (appearance) {
                    is HeadOnly -> {
                        editHeight(state, character, appearance.height)
                        editHead(raceAppearance, culture, appearance.head)
                        editSkin(raceAppearance, appearance.head.skin)
                    }

                    is HumanoidBody -> {
                        editHeight(state, character, appearance.height)
                        editBody(raceAppearance, character, appearance.body)
                        editHead(raceAppearance, culture, appearance.head)
                        editSkin(raceAppearance, appearance.head.skin)
                        editWings(raceAppearance, appearance.wings)
                    }

                    UndefinedAppearance -> doNothing()
                }
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            svg(frontSvg, 80)
            svg(backSvg, 80)
        })
    }
}

private fun FORM.editHeight(
    state: State,
    character: Character,
    maxHeight: Distance,
) {
    val race = state.getRaceStorage().getOrThrow(character.race)
    selectDistance("Max Height", HEIGHT, maxHeight, race.height.getMin(), race.height.getMax(), Distance(10))
    showCurrentHeight(state, character, maxHeight)
}

private fun FORM.editBody(
    raceAppearance: RaceAppearance,
    character: Character,
    body: Body,
) {
    h2 { +"Body" }
    selectValue("Shape", BODY_SHAPE, getAvailableBodyShapes(character.gender), body.bodyShape, true)
    selectValue("Width", BODY_WIDTH, Size.entries, body.width, true)
    editFoot(raceAppearance.footOptions, body.foot)
}

private fun FORM.editFoot(footOptions: FootOptions, foot: Foot) {
    h2 { +"Feet" }

    selectOneOf("Type", FOOT, footOptions.footTypes, foot.getType(), true) { type ->
        label = type.name
        value = type.toString()
    }

    when (foot) {
        is ClawedFoot -> {
            selectOneOf("Claw Size", combine(FOOT, CLAWS, SIZE), footOptions.clawSizes, foot.size, true) { size ->
                label = size.name
                value = size.toString()
            }
            selectOneOf("Claw Color", combine(FOOT, CLAWS, COLOR), footOptions.clawColors, foot.color, true) { color ->
                label = color.name
                value = color.toString()
            }
        }

        else -> doNothing()
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
    selectOneOf("Type", combine(EAR, TYPE), raceAppearance.earsLayout, ears.getType(), true) { type ->
        label = type.name
        value = type.toString()
    }
    when (ears) {
        is NormalEars -> {
            selectOneOf("Ear Shape", combine(EAR, SHAPE), raceAppearance.earShapes, ears.shape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectValue("Ear Size", combine(EAR, SIZE), Size.entries, ears.size, true)
        }

        else -> doNothing()
    }
}

private fun FORM.editSkin(
    raceAppearance: RaceAppearance,
    skin: Skin,
) {
    h2 { +"Skin" }
    selectOneOf("Type", SKIN_TYPE, raceAppearance.skinTypes, skin.getType(), true) { type ->
        label = type.name
        value = type.toString()
    }
    when (skin) {
        is Fur -> selectColor("Color", SKIN_EXOTIC_COLOR, raceAppearance.furColors, skin.color)
        is Scales -> selectColor("Color", SKIN_EXOTIC_COLOR, raceAppearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", SKIN_EXOTIC_COLOR, raceAppearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectOneOf("Color", SKIN_COLOR, raceAppearance.normalSkinColors, skin.color, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getSkinColor(skinColor).toCode()
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
    selectOneOf("Type", BEARD, raceAppearance.hairOptions.beardTypes, beard.getType(), true) { option ->
        label = option.name
        value = option.toString()
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
    selectOneOf(
        "Style",
        combine(BEARD, STYLE),
        culture.appearanceStyle.beardStyles,
        beard.style.getType(),
        true
    ) { style ->
        label = style.name
        value = style.toString()
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
    current: GoateeStyle,
) {
    selectOneOf("Goatee", GOATEE_STYLE, culture.appearanceStyle.goateeStyles, current, true) { style ->
        label = style.name
        value = style.toString()
    }
}

private fun HtmlBlockTag.selectMoustacheStyle(
    culture: Culture,
    current: MoustacheStyle,
) {
    selectOneOf("Moustache", MOUSTACHE_STYLE, culture.appearanceStyle.moustacheStyles, current, true) { style ->
        label = style.name
        value = style.toString()
    }
}

private fun FORM.editEyes(
    raceAppearance: RaceAppearance,
    eyes: Eyes,
) {
    h2 { +"Eyes" }
    selectOneOf("Layout", combine(EYES, LAYOUT), raceAppearance.eyesLayout, eyes.getType(), true) { option ->
        label = option.name
        value = option.toString()
    }
    when (eyes) {
        is OneEye -> {
            editEye(raceAppearance.eyeOptions, eyes.eye)
            selectValue("Eye Size", EYE_SIZE, Size.entries, eyes.size, true)
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
    selectOneOf("Eye Type", combine(EYES, TYPE), eyeOptions.eyeTypes, eye.getType(), true) { option ->
        label = option.name
        value = option.toString()
    }
    when (eye) {
        is NormalEye -> {
            selectOneOf("Eye Shape", EYE_SHAPE, eyeOptions.eyeShapes, eye.eyeShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectOneOf("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes, eye.pupilShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectColor("Eye Color", PUPIL_COLOR, eyeOptions.eyeColors, eye.pupilColor)
            selectColor("Sclera Color", SCLERA_COLOR, eyeOptions.scleraColors, eye.scleraColor)
        }

        is SimpleEye -> {
            selectOneOf("Eye Shape", EYE_SHAPE, eyeOptions.eyeShapes, eye.eyeShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectColor("Eye Color", PUPIL_COLOR, eyeOptions.eyeColors, eye.color)
        }
    }
}

private fun FORM.editHair(
    raceAppearance: RaceAppearance,
    culture: Culture,
    hair: Hair,
) {
    h2 { +"Hair" }
    selectOneOf("Type", HAIR, raceAppearance.hairOptions.hairTypes, hair.getType(), true) { option ->
        label = option.name
        value = option.toString()
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
    selectOneOf(
        "Style",
        combine(HAIR, STYLE),
        culture.appearanceStyle.hairStyles,
        hair.style.getType(),
        true
    ) { style ->
        label = style.name
        value = style.toString()
    }
    selectColor("Color", combine(HAIR, COLOR), raceAppearance.hairOptions.colors, hair.color)

    when (hair.style) {
        is SidePart -> {
            selectValue("Side", SIDE_PART, Side.entries, hair.style.side, true)
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
    selectOneOf("Type", combine(MOUTH, TYPE), raceAppearance.mouthTypes, mouth.getType(), true) { option ->
        label = option.name
        value = option.toString()
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
    selectValue("Width", combine(MOUTH, WIDTH), Size.entries, size, true)
    selectValue("Teeth Color", TEETH_COLOR, TeethColor.entries, teethColor, true)
}

private fun FORM.editWings(
    raceAppearance: RaceAppearance,
    wings: Wings,
) {
    val wingOptions = raceAppearance.wingOptions

    h2 { +"Wings" }

    selectOneOf("Layout", combine(WING, LAYOUT), wingOptions.layouts, wings.getType(), true) { layout ->
        label = layout.name
        value = layout.toString()
    }

    when (wings) {
        NoWings -> doNothing()
        is OneWing -> {
            editWing(wingOptions, wings.wing, WING)
            selectValue("Wing Side", combine(WING, SIDE), Side.entries, wings.side, true)
        }

        is TwoWings -> editWing(wingOptions, wings.wing, WING)
        is DifferentWings -> {
            editWing(wingOptions, wings.left, combine(LEFT, WING))
            editWing(wingOptions, wings.right, combine(RIGHT, WING))
        }
    }
}

private fun FORM.editWing(
    wingOptions: WingOptions,
    wing: Wing,
    param: String,
) {
    selectWingType(wingOptions, wing.getType(), combine(param, TYPE))

    when (wing) {
        is BatWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.batColors, wing.color)
        is BirdWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.birdColors, wing.color)
        is ButterflyWing -> selectColor("Wing Color", combine(param, COLOR), wingOptions.butterflyColors, wing.color)
    }
}

private fun FORM.selectWingType(
    wingOptions: WingOptions,
    currentType: WingType,
    param: String,
) {
    selectOneOf("Type", param, wingOptions.types, currentType, true) { type ->
        label = type.name
        value = type.toString()
    }
}
