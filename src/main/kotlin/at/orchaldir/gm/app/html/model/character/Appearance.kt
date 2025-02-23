package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.routes.character.showCurrentHeight
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.*

// edit

fun FORM.editAppearance(
    state: State,
    raceAppearance: RaceAppearance,
    appearance: Appearance,
    character: Character,
    culture: Culture,
) {
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
    selectOneOf("Type", combine(SKIN, TYPE), raceAppearance.skinTypes, skin.getType(), true) { type ->
        label = type.name
        value = type.toString()
    }
    when (skin) {
        is Fur -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.furColors, skin.color)
        is Scales -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectOneOf("Color", combine(SKIN, COLOR), raceAppearance.normalSkinColors, skin.color, true) { skinColor ->
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
    selectOneOf("Layout", combine(EYE, LAYOUT), raceAppearance.eyesLayout, eyes.getType(), true) { option ->
        label = option.name
        value = option.toString()
    }
    when (eyes) {
        is OneEye -> {
            editEye(raceAppearance.eyeOptions, eyes.eye)
            selectValue("Eye Size", combine(EYE, SIZE), Size.entries, eyes.size, true)
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
    selectOneOf("Eye Type", combine(EYE, TYPE), eyeOptions.eyeTypes, eye.getType(), true) { option ->
        label = option.name
        value = option.toString()
    }
    when (eye) {
        is NormalEye -> {
            selectOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectOneOf("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes, eye.pupilShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectColor("Eye Color", combine(PUPIL, COLOR), eyeOptions.eyeColors, eye.pupilColor)
            selectColor("Sclera Color", combine(PUPIL, SCLERA), eyeOptions.scleraColors, eye.scleraColor)
        }

        is SimpleEye -> {
            selectOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectColor("Eye Color", combine(PUPIL, COLOR), eyeOptions.eyeColors, eye.color)
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

// parse

fun parseAppearance(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val height = parseHeight(parameters, config)
    val skin = parseSkin(parameters, config)

    return when (parameters[APPEARANCE]) {
        AppearanceType.HeadOnly.toString() -> HeadOnly(parseHead(parameters, config, character, skin), height)
        AppearanceType.Body.toString() -> HumanoidBody(
            parseBody(parameters, config, skin),
            parseHead(parameters, config, character, skin),
            height,
            parseWings(parameters, config),
        )

        else -> UndefinedAppearance
    }
}

private fun parseHeight(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = parseDistance(parameters, HEIGHT, config.heightDistribution.center.millimeters)

private fun parseBody(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    skin: Skin,
): Body {
    if (parameters.contains(BODY_SHAPE)) {
        return Body(
            parse(parameters, BODY_SHAPE, BodyShape.Rectangle),
            parseFoot(parameters, config),
            parse(parameters, BODY_WIDTH, Size.Medium),
            skin,
        )
    }

    return generateBody(config, skin)
}

private fun parseFoot(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[FOOT]) {
    FootType.Normal.toString() -> NormalFoot
    FootType.Clawed.toString() -> ClawedFoot(
        config.appearanceOptions.footOptions.clawNumber,
        parse(parameters, combine(FOOT, CLAWS, SIZE), Size.Medium),
        parse(parameters, combine(FOOT, CLAWS, COLOR), Color.Black),
    )

    else -> generateFoot(config)
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
    return when (parameters[BEARD]) {
        BeardType.None.toString() -> NoBeard
        BeardType.Normal.toString() -> {
            return NormalBeard(
                when (parameters[combine(BEARD, STYLE)]) {
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
                parse(parameters, combine(BEARD, COLOR), Color.Red),
            )
        }

        else -> generateBeard(config, hair)
    }
}

private fun parseEars(parameters: Parameters, config: AppearanceGeneratorConfig): Ears {
    return when (parameters[combine(EAR, TYPE)]) {
        EarsLayout.NoEars.toString() -> NoEars
        EarsLayout.NormalEars.toString() -> {
            val shape = parse(parameters, combine(EAR, SHAPE), EarShape.Round)
            val size = parse(parameters, combine(EAR, SIZE), Size.Medium)
            return NormalEars(shape, size)
        }

        else -> generateEars(config)
    }
}

private fun parseEyes(parameters: Parameters, config: AppearanceGeneratorConfig): Eyes {
    return when (parameters[combine(EYE, LAYOUT)]) {
        EyesLayout.NoEyes.toString() -> NoEyes

        EyesLayout.OneEye.toString() -> {
            val eye = parseEye(parameters, config)
            val size = parse(parameters, combine(EYE, SIZE), Size.Medium)
            return OneEye(eye, size)
        }

        EyesLayout.TwoEyes.toString() -> {
            val eye = parseEye(parameters, config)
            return TwoEyes(eye)
        }

        else -> generateEyes(config)
    }
}

private fun parseEye(parameters: Parameters, config: AppearanceGeneratorConfig) =
    when (parameters[combine(EYE, TYPE)]) {
        EyeType.Simple.toString() -> SimpleEye(
            parse(parameters, combine(EYE, SHAPE), EyeShape.Ellipse),
            parse(parameters, combine(PUPIL, COLOR), Color.Green),
        )

        EyeType.Normal.toString() -> NormalEye(
            parse(parameters, combine(EYE, SHAPE), EyeShape.Ellipse),
            parse(parameters, combine(PUPIL, SHAPE), PupilShape.Circle),
            parse(parameters, combine(PUPIL, COLOR), Color.Green),
            parse(parameters, combine(SCLERA, COLOR), Color.White),
        )

        else -> generateEye(config)
    }

private fun parseHair(parameters: Parameters, config: AppearanceGeneratorConfig): Hair {
    return when (parameters[HAIR]) {
        HairType.None.toString() -> NoHair
        HairType.Normal.toString() -> {
            return NormalHair(
                when (parameters[combine(HAIR, STYLE)]) {
                    HairStyleType.BuzzCut.toString() -> BuzzCut
                    HairStyleType.FlatTop.toString() -> FlatTop
                    HairStyleType.MiddlePart.toString() -> MiddlePart
                    HairStyleType.SidePart.toString() -> SidePart(
                        parse(parameters, SIDE_PART, Side.Left),
                    )

                    HairStyleType.Spiked.toString() -> Spiked
                    else -> ShavedHair
                },
                parse(parameters, combine(HAIR, COLOR), Color.Red),
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
    return when (parameters[combine(MOUTH, TYPE)]) {
        MouthType.NoMouth.toString() -> NoMouth
        MouthType.NormalMouth.toString() -> {
            if (character.gender == Gender.Female) {
                return FemaleMouth(
                    parse(parameters, combine(MOUTH, WIDTH), Size.Medium),
                    parse(parameters, LIP_COLOR, Color.Red),
                    parse(parameters, TEETH_COLOR, TeethColor.White),
                )
            }
            return NormalMouth(
                parseBeard(parameters, config, hair),
                parse(parameters, combine(MOUTH, WIDTH), Size.Medium),
                parse(parameters, TEETH_COLOR, TeethColor.White),
            )
        }

        else -> generateMouth(config, hair)
    }
}

private fun parseSkin(parameters: Parameters, config: AppearanceGeneratorConfig): Skin {
    return when (parameters[combine(SKIN, TYPE)]) {
        SkinType.Fur.toString() -> {
            return Fur(parseExoticColor(parameters))
        }

        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters))
        }

        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters))
        }

        SkinType.Normal.toString() -> {
            val color = parse(parameters, combine(SKIN, COLOR), SkinColor.Medium)
            return NormalSkin(color)
        }

        else -> generateSkin(config)
    }
}

private fun parseExoticColor(parameters: Parameters) =
    parse(parameters, combine(SKIN, EXOTIC, COLOR), Color.Red)

private fun parseWings(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = when (parameters[combine(WING, LAYOUT)]) {
    WingsLayout.None.toString() -> NoWings
    WingsLayout.One.toString() -> OneWing(
        parseWing(parameters, config, WING),
        parse(parameters, combine(WING, SIDE), Side.Right),
    )

    WingsLayout.Two.toString() -> TwoWings(
        parseWing(parameters, config, WING),
    )

    WingsLayout.Different.toString() -> DifferentWings(
        parseWing(parameters, config, combine(LEFT, WING)),
        parseWing(parameters, config, combine(RIGHT, WING)),
    )

    else -> generateWings(config)
}

private fun parseWing(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    param: String,
) = when (parameters[combine(param, TYPE)]) {
    WingType.Bat.toString() -> BatWing(parse(parameters, combine(param, COLOR), DEFAULT_BAT_COLOR))
    WingType.Bird.toString() -> BirdWing(parse(parameters, combine(param, COLOR), DEFAULT_BIRD_COLOR))
    WingType.Butterfly.toString() -> ButterflyWing(parse(parameters, combine(param, COLOR), DEFAULT_BUTTERFLY_COLOR))

    else -> generateWing(config)
}