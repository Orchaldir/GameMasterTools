package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectOneOf
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// edit

fun FORM.editHead(
    raceAppearance: RaceAppearance,
    culture: Culture,
    head: Head,
) {
    editEars(raceAppearance, head.ears)
    editEyes(raceAppearance, head.eyes)
    editHair(raceAppearance, culture, head.hair)
    editHorns(raceAppearance, head.horns)
    editMouth(raceAppearance, culture, head.mouth)
}

private fun FORM.editEars(raceAppearance: RaceAppearance, ears: Ears) {
    h2 { +"Ears" }

    selectOneOf("Type", combine(EAR, TYPE), raceAppearance.earsLayout, ears.getType(), true)

    when (ears) {
        is NormalEars -> {
            selectOneOf("Ear Shape", combine(EAR, SHAPE), raceAppearance.earShapes, ears.shape, true)
            selectValue("Ear Size", combine(EAR, SIZE), Size.entries, ears.size, true)
        }

        else -> doNothing()
    }
}

private fun FORM.editBeard(
    raceAppearance: RaceAppearance,
    culture: Culture,
    beard: Beard,
) {
    h2 { +"Beard" }

    selectOneOf("Type", BEARD, raceAppearance.hairOptions.beardTypes, beard.getType(), true)

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
    )
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
    selectOneOf("Goatee", GOATEE_STYLE, culture.appearanceStyle.goateeStyles, current, true)
}

private fun HtmlBlockTag.selectMoustacheStyle(
    culture: Culture,
    current: MoustacheStyle,
) {
    selectOneOf("Moustache", MOUSTACHE_STYLE, culture.appearanceStyle.moustacheStyles, current, true)
}

private fun FORM.editEyes(
    raceAppearance: RaceAppearance,
    eyes: Eyes,
) {
    h2 { +"Eyes" }

    selectOneOf("Layout", combine(EYE, LAYOUT), raceAppearance.eyesLayout, eyes.getType(), true)

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
    selectOneOf("Eye Type", combine(EYE, TYPE), eyeOptions.eyeTypes, eye.getType(), true)

    when (eye) {
        is NormalEye -> {
            selectOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape, true)
            selectOneOf("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes, eye.pupilShape, true)
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

    selectOneOf("Type", HAIR, raceAppearance.hairOptions.hairTypes, hair.getType(), true)

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
    )
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
    val mouthOptions = raceAppearance.mouthOptions

    selectOneOf("Type", combine(MOUTH, TYPE), mouthOptions.mouthTypes, mouth.getType(), true)

    when (mouth) {
        is NormalMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            editBeard(raceAppearance, culture, mouth.beard)
        }

        is FemaleMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            selectColor("Lip Color", combine(LIP, COLOR), culture.appearanceStyle.lipColors, mouth.color)
        }

        is Beak -> {
            selectOneOf("Beak Shape", combine(BEAK, SHAPE), mouthOptions.beakShapes, mouth.shape, true)
            selectOneOf("Beak Color", combine(BEAK, COLOR), mouthOptions.beakColors, mouth.color, true)
        }

        NoMouth -> doNothing()
    }
}

private fun FORM.editSimpleMouth(size: Size, teethColor: TeethColor) {
    selectValue("Width", combine(MOUTH, WIDTH), Size.entries, size, true)
    selectValue("Teeth Color", TEETH_COLOR, TeethColor.entries, teethColor, true)
}

// parse

fun parseHead(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
    skin: Skin,
): Head {
    val ears = parseEars(parameters, config)
    val eyes = parseEyes(parameters, config)
    val hair = parseHair(parameters, config)
    val horns = parseHorns(parameters, config)
    val mouth = parseMouth(parameters, config, character, hair)

    return Head(ears, eyes, hair, horns, mouth, skin)
}

private fun parseBeard(parameters: Parameters, config: AppearanceGeneratorConfig, hair: Hair): Beard {
    val style = config.appearanceStyle

    return when (parameters[BEARD]) {
        BeardType.None.toString() -> NoBeard
        BeardType.Normal.toString() -> {
            return NormalBeard(
                when (parameters[combine(BEARD, STYLE)]) {
                    BeardStyleType.Goatee.toString() -> {
                        Goatee(parseGoateeStyle(parameters, config, style))
                    }

                    BeardStyleType.GoateeAndMoustache.toString() -> GoateeAndMoustache(
                        parseMoustacheStyle(parameters, config, style),
                        parseGoateeStyle(parameters, config, style),
                    )

                    BeardStyleType.Moustache.toString() -> Moustache(
                        parseMoustacheStyle(parameters, config, style),
                    )

                    BeardStyleType.Shaved.toString() -> ShavedBeard

                    else -> Goatee(GoateeStyle.Goatee)
                },
                parseAppearanceColor(parameters, BEARD, config, config.appearanceOptions.hairOptions.colors),
            )
        }

        else -> generateBeard(config, hair)
    }
}

private fun parseMoustacheStyle(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    style: AppearanceStyle,
) = parseAppearanceOption(parameters, MOUSTACHE_STYLE, config, style.moustacheStyles)

private fun parseGoateeStyle(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    style: AppearanceStyle,
) = parseAppearanceOption(parameters, GOATEE_STYLE, config, style.goateeStyles)

private fun parseEars(parameters: Parameters, config: AppearanceGeneratorConfig): Ears {
    return when (parameters[combine(EAR, TYPE)]) {
        EarsLayout.NoEars.toString() -> NoEars
        EarsLayout.NormalEars.toString() -> {
            val options = config.appearanceOptions
            val shape = parseAppearanceOption(parameters, combine(EAR, SHAPE), config, options.earShapes)
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

private fun parseEye(parameters: Parameters, config: AppearanceGeneratorConfig): Eye {
    val options = config.appearanceOptions.eyeOptions

    return when (parameters[combine(EYE, TYPE)]) {
        EyeType.Simple.toString() -> SimpleEye(
            parseEyeShape(parameters, config, options),
            parseAppearanceColor(parameters, PUPIL, config, options.eyeColors),
        )

        EyeType.Normal.toString() -> {
            NormalEye(
                parseEyeShape(parameters, config, options),
                parseAppearanceOption(parameters, combine(PUPIL, SHAPE), config, options.pupilShapes),
                parseAppearanceColor(parameters, PUPIL, config, options.eyeColors),
                parseAppearanceColor(parameters, SCLERA, config, options.scleraColors),
            )
        }

        else -> generateEye(config)
    }
}

private fun parseEyeShape(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    options: EyeOptions,
) = parseAppearanceOption(parameters, combine(EYE, SHAPE), config, options.eyeShapes)

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
                parseAppearanceColor(parameters, HAIR, config, config.appearanceOptions.hairOptions.colors),
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
                    parseAppearanceColor(parameters, LIP, config, config.appearanceStyle.lipColors),
                    parse(parameters, TEETH_COLOR, TeethColor.White),
                )
            }
            return NormalMouth(
                parseBeard(parameters, config, hair),
                parse(parameters, combine(MOUTH, WIDTH), Size.Medium),
                parse(parameters, TEETH_COLOR, TeethColor.White),
            )
        }

        MouthType.Beak.toString() -> {
            val mouthOptions = config.appearanceOptions.mouthOptions
            Beak(
                parseAppearanceOption(parameters, combine(BEAK, SHAPE), config, mouthOptions.beakShapes),
                parseAppearanceColor(parameters, BEAK, config, mouthOptions.beakColors),
            )
        }

        else -> generateMouth(config, hair)
    }
}
