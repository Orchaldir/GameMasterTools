package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.APPEARANCE_TYPE
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.style.HairStyleType
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.selector.getName
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
private const val BEARD_TYPE = "beard"
private const val BEARD_STYLE = "beard_style"
private const val BEARD_COLOR = "beard_color"
private const val GOATEE_STYLE = "goatee"
private const val MOUSTACHE_STYLE = "moustache"
private const val BODY_SHAPE = "body_shape"
private const val BODY_WIDTH = "body_width"

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
        post<Characters.Appearance.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(preview.id)
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
            val character = state.characters.getOrThrow(update.id)
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
            val character = state.characters.getOrThrow(update.id)
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
    val race = state.races.getOrThrow(character.race)
    val culture = state.cultures.getOrThrow(character.culture)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Appearance.Preview(character.id))
    val updateLink = call.application.href(Characters.Appearance.Update(character.id))
    val generateLink = call.application.href(Characters.Appearance.Generate(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, appearance)

    simpleHtml("Edit Appearance: ${state.getName(character)}") {
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
            selectOneOf("Appearance Type", APPEARANCE_TYPE, race.appearance.appearanceType, true) { type ->
                label = type.name
                value = type.toString()
                selected = when (type) {
                    AppearanceType.Body -> appearance is HumanoidBody
                    AppearanceType.HeadOnly -> appearance is HeadOnly
                }
            }
            when (appearance) {
                is HeadOnly -> {
                    showHeadEditor(race, culture, appearance.head)
                    showSkinEditor(race, appearance.head.skin)
                }

                is HumanoidBody -> {
                    showBodyEditor(character, appearance.body)
                    showHeadEditor(race, culture, appearance.head)
                    showSkinEditor(race, appearance.head.skin)
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
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.showBodyEditor(
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

private fun FORM.showHeadEditor(
    race: Race,
    culture: Culture,
    head: Head,
) {
    showEarsEditor(race, head.ears)
    showEyesEditor(race, head.eyes)
    showHairEditor(race, culture, head.hair)
    showMouthEditor(race, culture, head.mouth)
}

private fun FORM.showEarsEditor(race: Race, ears: Ears) {
    h2 { +"Ears" }
    selectOneOf("Type", EAR_TYPE, race.appearance.earsLayout, true) { type ->
        label = type.name
        value = type.toString()
        selected = when (type) {
            EarsLayout.NoEars -> ears is NoEars
            EarsLayout.NormalEars -> ears is NormalEars
        }
    }
    when (ears) {
        is NormalEars -> {
            selectOneOf("Ear Shape", EAR_SHAPE, race.appearance.earShapes, true) { shape ->
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
    selectOneOf("Type", SKIN_TYPE, race.appearance.skinTypes, true) { type ->
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
            selectOneOf("Color", SKIN_COLOR, race.appearance.normalSkinColors, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                selected = skin.color == skinColor
                val bgColor = RENDER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

private fun FORM.showBeardEditor(
    race: Race,
    culture: Culture,
    beard: Beard,
) {
    h2 { +"Beard" }
    selectOneOf("Type", BEARD_TYPE, race.appearance.hairOptions.beardTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            BeardType.None -> beard is NoBeard
            BeardType.Normal -> beard is NormalBeard
        }
    }
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> showNormalBeardEditor(race, culture, beard)
    }
}

private fun FORM.showNormalBeardEditor(
    race: Race,
    culture: Culture,
    beard: NormalBeard,
) {
    selectOneOf("Style", BEARD_STYLE, culture.styleOptions.beardStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = when (style) {
            BeardStyleType.Goatee -> beard.style is Goatee
            BeardStyleType.GoateeAndMoustache -> beard.style is GoateeAndMoustache
            BeardStyleType.Moustache -> beard.style is Moustache
            BeardStyleType.Shaved -> beard.style is ShavedBeard
        }
    }
    selectColor("Color", BEARD_COLOR, race.appearance.hairOptions.colors, beard.color)

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
    selectOneOf("Goatee", GOATEE_STYLE, culture.styleOptions.goateeStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = style == goateeStyle
    }
}

private fun HtmlBlockTag.selectMoustacheStyle(
    culture: Culture,
    moustacheStyle: MoustacheStyle,
) {
    selectOneOf("Moustache", MOUSTACHE_STYLE, culture.styleOptions.moustacheStyle, true) { style ->
        label = style.name
        value = style.toString()
        selected = style == moustacheStyle
    }
}

private fun FORM.showEyesEditor(
    race: Race,
    eyes: Eyes,
) {
    h2 { +"Eyes" }
    selectOneOf("Layout", EYES_LAYOUT, race.appearance.eyesLayout, true) { option ->
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

private fun FORM.showHairEditor(
    race: Race,
    culture: Culture,
    hair: Hair,
) {
    h2 { +"Hair" }
    selectOneOf("Type", HAIR_TYPE, race.appearance.hairOptions.hairTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            HairType.None -> hair is NoHair
            HairType.Normal -> hair is NormalHair
        }
    }
    when (hair) {
        NoHair -> doNothing()
        is NormalHair -> showNormalHairEditor(race, culture, hair)
    }
}

private fun FORM.showNormalHairEditor(
    race: Race,
    culture: Culture,
    hair: NormalHair,
) {
    selectOneOf("Style", HAIR_STYLE, culture.styleOptions.hairStyles, true) { style ->
        label = style.name
        value = style.toString()
        selected = when (style) {
            HairStyleType.Afro -> hair.style is Afro
            HairStyleType.BuzzCut -> hair.style is BuzzCut
            HairStyleType.FlatTop -> hair.style is FlatTop
            HairStyleType.MiddlePart -> hair.style is MiddlePart
            HairStyleType.Shaved -> hair.style is ShavedHair
            HairStyleType.SidePart -> hair.style is SidePart
            HairStyleType.Spiked -> hair.style is Spiked
        }
    }
    selectColor("Color", HAIR_COLOR, race.appearance.hairOptions.colors, hair.color)

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

private fun FORM.showMouthEditor(
    race: Race,
    culture: Culture,
    mouth: Mouth,
) {
    h2 { +"Mouth" }
    selectOneOf("Type", MOUTH_TYPE, race.appearance.mouthTypes, true) { option ->
        label = option.name
        value = option.toString()
        selected = when (option) {
            MouthType.NoMouth -> mouth is NoMouth
            MouthType.NormalMouth -> mouth is NormalMouth || mouth is FemaleMouth
        }
    }
    when (mouth) {
        is NormalMouth -> {
            showSimpleMouthEditor(mouth.width, mouth.teethColor)
            showBeardEditor(race, culture, mouth.beard)
        }

        is FemaleMouth -> {
            showSimpleMouthEditor(mouth.width, mouth.teethColor)
            selectColor("Lip Color", LIP_COLOR, culture.styleOptions.lipColors, mouth.color)
        }

        else -> doNothing()
    }
}

private fun createGenerationConfig(state: State, character: Character): AppearanceGeneratorConfig {
    val race = state.races.getOrThrow(character.race)
    val culture = state.cultures.getOrThrow(character.culture)

    return AppearanceGeneratorConfig(
        RandomNumberGenerator(Random),
        state.rarityGenerator,
        character,
        race.appearance,
        culture.styleOptions
    )
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

private fun generateAppearance(
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val type = config.generate(config.appearanceOptions.appearanceType)
    val parameters = parametersOf(APPEARANCE_TYPE, type.toString())

    return parseAppearance(parameters, config, character)
}

private fun parseAppearance(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val skin = parseSkin(parameters, config)

    return when (parameters[APPEARANCE_TYPE]) {
        AppearanceType.HeadOnly.toString() -> HeadOnly(parseHead(parameters, config, character, skin), Distance(0.2f))
        AppearanceType.Body.toString() -> HumanoidBody(
            parseBody(parameters, config, skin),
            parseHead(parameters, config, character, skin),
            Distance(1.8f)
        )

        else -> UndefinedAppearance
    }
}

private fun parseBody(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    skin: Skin,
): Body {
    if (parameters.contains(BODY_SHAPE)) {
        return Body(
            parse(parameters, BODY_SHAPE, BodyShape.Rectangle),
            parse(parameters, BODY_WIDTH, Size.Medium),
            skin,
        )
    }

    return generateBody(config, skin)
}

private fun parseHead(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
    skin: Skin,
): Head {
    val ears = parseEars(parameters, config)
    val eyes = parseEyes(parameters, config)
    val hair = parseHair(parameters, config)
    val mouth = parseMouth(parameters, config, character, hair)

    return Head(ears, eyes, hair, mouth, skin)
}

private fun parseBeard(parameters: Parameters, config: AppearanceGeneratorConfig, hair: Hair): Beard {
    return when (parameters[BEARD_TYPE]) {
        BeardType.None.toString() -> NoBeard
        BeardType.Normal.toString() -> {
            return NormalBeard(
                when (parameters[BEARD_STYLE]) {
                    BeardStyleType.Goatee.toString() -> Goatee(
                        parse(parameters, GOATEE_STYLE, GoateeStyle.Goatee),
                    )

                    BeardStyleType.GoateeAndMoustache.toString() -> GoateeAndMoustache(
                        parse(parameters, MOUSTACHE_STYLE, MoustacheStyle.Handlebar),
                        parse(parameters, GOATEE_STYLE, GoateeStyle.Goatee),
                    )

                    BeardStyleType.Moustache.toString() -> Moustache(
                        parse(parameters, MOUSTACHE_STYLE, MoustacheStyle.Handlebar),
                    )

                    BeardStyleType.Shaved.toString() -> ShavedBeard

                    else -> Goatee(GoateeStyle.Goatee)
                },
                parse(parameters, BEARD_COLOR, Color.Red),
            )
        }

        else -> generateBeard(config, hair)
    }
}

private fun parseEars(parameters: Parameters, config: AppearanceGeneratorConfig): Ears {
    return when (parameters[EAR_TYPE]) {
        EarsLayout.NoEars.toString() -> NoEars
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
        EyesLayout.NoEyes.toString() -> NoEyes

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
        HairType.None.toString() -> NoHair
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
                    else -> ShavedHair
                },
                parse(parameters, HAIR_COLOR, Color.Red),
            )
        }

        else -> generateHair(config)
    }
}

private fun parseMouth(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
    hair: Hair,
): Mouth {
    return when (parameters[MOUTH_TYPE]) {
        MouthType.NoMouth.toString() -> NoMouth
        MouthType.NormalMouth.toString() -> {
            if (character.gender == Gender.Female) {
                return FemaleMouth(
                    parse(parameters, MOUTH_WIDTH, Size.Medium),
                    parse(parameters, LIP_COLOR, Color.Red),
                    parse(parameters, TEETH_COLOR, TeethColor.White),
                )
            }
            return NormalMouth(
                parseBeard(parameters, config, hair),
                parse(parameters, MOUTH_WIDTH, Size.Medium),
                parse(parameters, TEETH_COLOR, TeethColor.White),
            )
        }

        else -> generateMouth(config, hair)
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
