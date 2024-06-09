package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.race.appearance.HairStyleType
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
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
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

private const val TYPE = "type"
private const val HEAD = "head"
private const val SKIN_TYPE = "skin"
private const val EXOTIC_COLOR = "exotic_color"
private const val SKIN_COLOR = "skin_color"
private const val EAR_TYPE = "ear_type"
private const val EAR_SHAPE = "ear_shape"
private const val EAR_SIZE = "ear_size"
private const val EYES_LAYOUT = "eyes_layout"
private const val EYE_SIZE = "eye_size"
private const val EYE_SHAPE = "eye_shape"
private const val PUPIL_SHAPE = "pupil_shape"
private const val PUPIL_COLOR = "pupil_color"
private const val SCLERA_COLOR = "sclera_color"
private const val HAIR_TYPE = "hair"
private const val HAIR_STYLE = "hair_style"
private const val HAIR_COLOR = "hair_color"
private const val SIDE_PART = "side_part"
private const val MOUTH_TYPE = "mouth"
private const val MOUTH_WIDTH = "mouth_width"
private const val TEETH_COLOR = "teeth_color"
private const val LIP_COLOR = "lip_color"

fun Application.configureAppearanceRouting() {
    routing {
        get<Characters.Appearance.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, character)
            }
        }
        post<Characters.Appearance.Preview> { edit ->
            logger.info { "Get preview for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character.race)
            val appearance = parseAppearance(formParameters, config)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, updatedCharacter)
            }
        }
        post<Characters.Appearance.Update> { update ->
            logger.info { "Update character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, update.id)
            val appearance = parseAppearance(formParameters, config)

            STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<Characters.Appearance.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(update.id)
            val config = createGenerationConfig(state, character.race)
            val newParameters = parametersOf(TYPE, HEAD)
            val appearance = parseAppearance(newParameters, config)
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
    val race = state.races.getOrThrow(character.race)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Appearance.Preview(character.id))
    val updateLink = call.application.href(Characters.Appearance.Update(character.id))
    val generateLink = call.application.href(Characters.Appearance.Generate(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, appearance)

    simpleHtml("Edit Appearance: ${character.name}") {
        svg(frontSvg, 20)
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
            field("Appearance Type") {
                select {
                    id = TYPE
                    name = TYPE
                    onChange = ON_CHANGE_SCRIPT
                    option {
                        label = "Undefined"
                        value = "Undefined"
                        selected = appearance is UndefinedAppearance
                    }
                    option {
                        label = "Head Only"
                        value = HEAD
                        selected = appearance is HeadOnly
                    }
                }
            }
            if (appearance is HeadOnly) {
                showSkinEditor(race, appearance.head.skin)
                showEarsEditor(race, appearance.head.ears)
                showEyesEditor(race, appearance.head.eyes)
                showHairEditor(race, appearance.head.hair)
                showMouthEditor(race, appearance.head.mouth)
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.showEarsEditor(race: Race, ears: Ears) {
    h2 { +"Ears" }
    selectEnum("Type", EAR_TYPE, race.appearance.earsLayout, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            EarsLayout.NoEars -> ears is NoEars
            EarsLayout.NormalEars -> ears is NormalEars
        }
    }
    when (ears) {
        is NormalEars -> {
            selectEnum("Ear Shape", EAR_SHAPE, race.appearance.earShapes, true) { shape ->
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

private fun FORM.showSkinEditor(
    race: Race,
    skin: Skin,
) {
    h2 { +"Skin" }
    selectEnum("Type", SKIN_TYPE, race.appearance.skinTypes, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            SkinType.Scales -> skin is Scales
            SkinType.Normal -> skin is NormalSkin
            SkinType.Exotic -> skin is ExoticSkin
        }
    }
    when (skin) {
        is Scales -> selectColor("Color", EXOTIC_COLOR, race.appearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", EXOTIC_COLOR, race.appearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectEnum("Color", SKIN_COLOR, race.appearance.normalSkinColors, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                selected = skin.color == skinColor
                val bgColor = RENDER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

private fun FORM.showEyesEditor(
    race: Race,
    eyes: Eyes,
) {
    h2 { +"Eyes" }
    selectEnum("Layout", EYES_LAYOUT, race.appearance.eyesLayout, true) { option ->
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
            showEyeEditor(race.appearance.eyeOptions, eyes.eye)
            selectEnum("Eye Size", EYE_SIZE, Size.entries, true) { c ->
                label = c.name
                value = c.toString()
                selected = eyes.size == c
            }
        }

        is TwoEyes -> {
            showEyeEditor(race.appearance.eyeOptions, eyes.eye)
        }

        else -> doNothing()
    }
}

private fun FORM.showEyeEditor(
    eyeOptions: EyeOptions,
    eye: Eye,
) {
    selectEnum("Eye Shape", EYE_SHAPE, eyeOptions.eyeShapes, true) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.eyeShape == shape
    }
    selectEnum("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes, true) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.pupilShape == shape
    }
    selectColor("Pupil Color", PUPIL_COLOR, eyeOptions.pupilColors, eye.pupilColor)
    selectColor("Sclera Color", SCLERA_COLOR, eyeOptions.scleraColors, eye.scleraColor)
}

private val noHair = NoHair

private fun FORM.showHairEditor(
    race: Race,
    hair: Hair,
) {
    h2 { +"Hair" }
    selectEnum("Type", HAIR_TYPE, race.appearance.hairOptions.types, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            HairType.None -> hair is NoHair
            HairType.Fire -> hair is FireHair
            HairType.Normal -> hair is NormalHair
        }
    }
    when (hair) {
        NoHair -> doNothing()
        is FireHair -> TODO()
        is NormalHair -> showNormalHairEditor(race, hair)
    }
}

private fun FORM.showNormalHairEditor(
    race: Race,
    hair: NormalHair,
) {
    selectEnum("Style", HAIR_STYLE, HairStyleType.entries, true) { style ->
        label = style.name
        value = style.toString()
        selected = when (style) {
            HairStyleType.Afro -> hair.style is Afro
            HairStyleType.BuzzCut -> hair.style is BuzzCut
            HairStyleType.FlatTop -> hair.style is FlatTop
            HairStyleType.MiddlePart -> hair.style is MiddlePart
            HairStyleType.Shaved -> hair.style is Shaved
            HairStyleType.SidePart -> hair.style is SidePart
            HairStyleType.Spiked -> hair.style is Spiked
        }
    }
    selectColor("Color", HAIR_COLOR, race.appearance.hairOptions.colors, hair.color)

    when (hair.style) {
        is SidePart -> {
            selectEnum("Side", HAIR_STYLE, Side.entries, true) { side ->
                label = side.name
                value = side.toString()
                selected = hair.style.side == side
            }
        }

        else -> doNothing()
    }
}

private fun FORM.showMouthEditor(
    race: Race,
    mouth: Mouth,
) {
    h2 { +"Mouth" }
    selectEnum("Type", MOUTH_TYPE, race.appearance.mouthTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            MouthType.NoMouth -> mouth is NoMouth
            MouthType.SimpleMouth -> mouth is SimpleMouth
            MouthType.FemaleMouth -> mouth is FemaleMouth
        }
    }
    when (mouth) {
        is SimpleMouth -> {
            showSimpleMouthEditor(mouth.width, mouth.teethColor)
        }

        is FemaleMouth -> {
            showSimpleMouthEditor(mouth.width, mouth.teethColor)
            selectEnum("Lip Color", LIP_COLOR, Color.entries, true) { color ->
                label = color.name
                value = color.toString()
                selected = mouth.color == color
            }
        }

        else -> doNothing()
    }
}

private fun createGenerationConfig(state: State, characterId: CharacterId): AppearanceGeneratorConfig {
    val character = state.characters.getOrThrow(characterId)

    return createGenerationConfig(state, character.race)
}

private fun createGenerationConfig(state: State, raceId: RaceId): AppearanceGeneratorConfig {
    val race = state.races.getOrThrow(raceId)

    return AppearanceGeneratorConfig(RandomNumberGenerator(Random), state.rarityGenerator, race.appearance)
}

private fun FORM.showSimpleMouthEditor(size: Size, teethColor: TeethColor) {
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

private fun parseAppearance(parameters: Parameters, config: AppearanceGeneratorConfig): Appearance {
    return when (parameters[TYPE]) {
        HEAD -> {
            val ears = parseEars(parameters, config)
            val eyes = parseEyes(parameters, config)
            val hair = parseHair(parameters, config)
            val mouth = parseMouth(parameters, config)
            val skin = parseSkin(parameters, config)
            val head = Head(ears, eyes, hair, mouth, skin)
            return HeadOnly(head, Distance(0.2f))
        }

        else -> UndefinedAppearance
    }
}

private fun parseEars(parameters: Parameters, config: AppearanceGeneratorConfig): Ears {
    return when (parameters[EAR_TYPE]) {
        EarsLayout.NormalEars.toString() -> {
            val shape = parse(parameters, EAR_SHAPE, EarShape.Round)
            val size = parse(parameters, EAR_SIZE, Size.Medium)
            return NormalEars(shape, size)
        }

        else -> generateEars(config)
    }
}

private fun parseEyes(parameters: Parameters, config: AppearanceGeneratorConfig): Eyes {
    return when (parameters[EYES_LAYOUT]) {
        EyesLayout.OneEye.toString() -> {
            val eye = parseEye(parameters)
            val size = parse(parameters, EYE_SIZE, Size.Medium)
            return OneEye(eye, size)
        }

        EyesLayout.TwoEyes.toString() -> {
            val eye = parseEye(parameters)
            return TwoEyes(eye)
        }

        else -> generateEyes(config)
    }
}

private fun parseEye(parameters: Parameters) = Eye(
    parse(parameters, EYE_SHAPE, EyeShape.Ellipse),
    parse(parameters, PUPIL_SHAPE, PupilShape.Circle),
    parse(parameters, PUPIL_COLOR, Color.Green),
    parse(parameters, SCLERA_COLOR, Color.White),
)

private fun parseHair(parameters: Parameters, config: AppearanceGeneratorConfig): Hair {

    return when (parameters[HAIR_TYPE]) {
        HairType.Normal.toString() -> {
            return NormalHair(
                when (parameters[HAIR_STYLE]) {
                    HairStyleType.Afro.toString() -> Afro
                    HairStyleType.BuzzCut.toString() -> BuzzCut
                    HairStyleType.FlatTop.toString() -> FlatTop
                    HairStyleType.MiddlePart.toString() -> MiddlePart
                    HairStyleType.SidePart.toString() -> SidePart(
                        parse(parameters, SIDE_PART, Side.Left),
                    )

                    HairStyleType.Spiked.toString() -> Spiked
                    else -> Shaved
                },
                    parse(parameters, HAIR_COLOR, Color.Red),
            )
        }

        HairType.Fire.toString() -> NoHair

        else -> NoHair
    }
}

private fun parseMouth(parameters: Parameters, config: AppearanceGeneratorConfig): Mouth {
    return when (parameters[MOUTH_TYPE]) {
        MouthType.SimpleMouth.toString() -> {
            return SimpleMouth(
                parse(parameters, MOUTH_WIDTH, Size.Medium),
                parse(parameters, TEETH_COLOR, TeethColor.White),
            )
        }

        MouthType.FemaleMouth.toString() -> {
            return FemaleMouth(
                parse(parameters, MOUTH_WIDTH, Size.Medium),
                parse(parameters, LIP_COLOR, Color.Red),
                parse(parameters, TEETH_COLOR, TeethColor.White),
            )
        }

        else -> generateMouth(config)
    }
}

private fun parseSkin(parameters: Parameters, config: AppearanceGeneratorConfig): Skin {
    return when (parameters[SKIN_TYPE]) {
        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters))
        }

        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters))
        }

        SkinType.Normal.toString() -> {
            val color = parse(parameters, SKIN_COLOR, SkinColor.Medium)
            return NormalSkin(color)
        }

        else -> generateSkin(config)
    }
}

private fun parseExoticColor(parameters: Parameters) =
    parse(parameters, EXOTIC_COLOR, Color.Red)
