package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectFromOneOf
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.fashion.AppearanceStyle
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// edit

fun FORM.editHead(
    state: State,
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    head: Head,
) {
    editEars(raceAppearance, head.ears)
    editEyes(raceAppearance, head.eyes)
    editHair(raceAppearance, style, head.hair)
    editHorns(state, raceAppearance, head.horns)
    editMouth(raceAppearance, style, head.mouth)
}

private fun FORM.editEars(raceAppearance: RaceAppearance, ears: Ears) {
    h2 { +"Ears" }

    selectFromOneOf("Type", combine(EAR, TYPE), raceAppearance.earsLayout, ears.getType(), true)

    when (ears) {
        is NormalEars -> {
            selectFromOneOf("Ear Shape", combine(EAR, SHAPE), raceAppearance.earShapes, ears.shape, true)
            selectValue("Ear Size", combine(EAR, SIZE), Size.entries, ears.size, true)
        }

        else -> doNothing()
    }
}

private fun FORM.editBeard(
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    beard: Beard,
) {
    h2 { +"Beard" }

    selectFromOneOf("Type", BEARD, raceAppearance.hair.beardTypes, beard.getType(), true)

    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> editNormalBeard(raceAppearance, style, beard)
    }
}

private fun FORM.editNormalBeard(
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    beard: NormalBeard,
) {
    selectFromOneOf(
        "Style",
        combine(BEARD, STYLE),
        style?.beardStyles,
        beard.style.getType(),
        true
    )
    selectColor("Color", combine(BEARD, COLOR), raceAppearance.hair.colors, beard.color)

    when (beard.style) {
        is Goatee -> selectGoateeStyle(style, beard.style.goateeStyle)
        is GoateeAndMoustache -> {
            selectGoateeStyle(style, beard.style.goateeStyle)
            selectMoustacheStyle(style, beard.style.moustacheStyle)
        }

        is Moustache -> selectMoustacheStyle(style, beard.style.moustacheStyle)
        ShavedBeard -> doNothing()
    }
}

private fun HtmlBlockTag.selectGoateeStyle(
    style: AppearanceStyle?,
    current: GoateeStyle,
) {
    selectFromOneOf("Goatee", GOATEE_STYLE, style?.goateeStyles, current, true)
}

private fun HtmlBlockTag.selectMoustacheStyle(
    style: AppearanceStyle?,
    current: MoustacheStyle,
) {
    selectFromOneOf("Moustache", MOUSTACHE_STYLE, style?.moustacheStyles, current, true)
}

private fun FORM.editEyes(
    raceAppearance: RaceAppearance,
    eyes: Eyes,
) {
    h2 { +"Eyes" }

    selectFromOneOf("Layout", combine(EYE, LAYOUT), raceAppearance.eyesLayout, eyes.getType(), true)

    when (eyes) {
        is OneEye -> {
            editEye(raceAppearance.eye, eyes.eye)
            selectValue("Eye Size", combine(EYE, SIZE), Size.entries, eyes.size, true)
        }

        is TwoEyes -> {
            editEye(raceAppearance.eye, eyes.eye)
        }

        else -> doNothing()
    }
}

private fun FORM.editEye(
    eyeOptions: EyeOptions,
    eye: Eye,
) {
    selectFromOneOf("Eye Type", combine(EYE, TYPE), eyeOptions.eyeTypes, eye.getType(), true)

    when (eye) {
        is NormalEye -> editNormalEye(eyeOptions, eye)

        is SimpleEye -> {
            selectFromOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape, true) { shape ->
                label = shape.name
                value = shape.toString()
            }
            selectColor("Eye Color", combine(PUPIL, COLOR), eyeOptions.eyeColors, eye.color)
        }
    }
}

fun HtmlBlockTag.editNormalEye(
    eyeOptions: EyeOptions,
    eye: NormalEye,
) {
    showDetails("Eye", true) {
        selectFromOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape, true)
        selectFromOneOf("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes, eye.pupilShape, true)
        selectColor("Eye Color", combine(PUPIL, COLOR), eyeOptions.eyeColors, eye.pupilColor)
        selectColor("Sclera Color", combine(PUPIL, SCLERA), eyeOptions.scleraColors, eye.scleraColor)
    }
}

private fun FORM.editHair(
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    hair: Hair,
) {
    h2 { +"Hair" }

    selectFromOneOf("Type", HAIR, raceAppearance.hair.hairTypes, hair.getType(), true)

    when (hair) {
        NoHair -> doNothing()
        is NormalHair -> editNormalHair(raceAppearance, style, hair)
    }
}

private fun FORM.editNormalHair(
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    hair: NormalHair,
) {
    selectFromOneOf(
        "Haircut",
        combine(HAIR, STYLE),
        style?.hairStyles,
        hair.cut.getType(),
        true,
    )
    selectColor("Color", combine(HAIR, COLOR), raceAppearance.hair.colors, hair.color)

    when (hair.cut) {
        is Bun -> {
            selectFromOneOf(
                "Bun Style",
                combine(BUN, STYLE),
                style?.bunStyles,
                hair.cut.style,
                true,
            )
            selectValue("Bun Size", combine(BUN, SIZE), Size.entries, hair.cut.size, true)
        }

        is LongHairCut -> {
            selectFromOneOf(
                "Long Hair Style",
                combine(LONG, STYLE),
                style?.longHairStyles,
                hair.cut.style,
                true,
            )
            selectHairLength(style, hair.cut.length)
        }

        is Ponytail -> {
            selectFromOneOf(
                "Ponytail Style",
                combine(PONYTAIL, STYLE),
                style?.ponytailStyles,
                hair.cut.style,
                true,
            )
            selectFromOneOf(
                "Ponytail Position",
                combine(PONYTAIL, POSITION),
                style?.ponytailPositions,
                hair.cut.position,
                true,
            )
            selectHairLength(style, hair.cut.length)
        }

        is ShortHairCut -> selectFromOneOf(
            "Short Hair Style",
            combine(SHORT, STYLE),
            style?.shortHairStyles,
            hair.cut.style,
            true,
        )
    }
}

private fun FORM.selectHairLength(
    style: AppearanceStyle?,
    length: HairLength,
) {
    selectFromOneOf(
        "Length",
        combine(HAIR, LENGTH),
        style?.hairLengths,
        length,
        true,
    )
}

private fun FORM.editMouth(
    raceAppearance: RaceAppearance,
    style: AppearanceStyle?,
    mouth: Mouth,
) {
    h2 { +"Mouth" }
    val mouthOptions = raceAppearance.mouth

    selectFromOneOf("Type", combine(MOUTH, TYPE), mouthOptions.mouthTypes, mouth.getType(), true)

    when (mouth) {
        NoMouth -> doNothing()

        is NormalMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            editBeard(raceAppearance, style, mouth.beard)
        }

        is FemaleMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            val colors = style?.lipColors ?: OneOf(Color.entries)
            selectColor("Lip Color", combine(LIP, COLOR), colors, mouth.color)
        }

        is Beak -> {
            selectFromOneOf("Beak Shape", combine(BEAK, SHAPE), mouthOptions.beakShapes, mouth.shape, true)
            selectFromOneOf("Beak Color", combine(BEAK, COLOR), mouthOptions.beakColors, mouth.color, true)
        }

        is Snout -> {
            selectFromOneOf("Snout Shape", combine(SNOUT, SHAPE), mouthOptions.snoutShapes, mouth.shape, true)
            selectFromOneOf("Snout Color", combine(SNOUT, COLOR), mouthOptions.snoutColors, mouth.color, true)
        }
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
): Head {
    val ears = parseEars(parameters, config)
    val eyes = parseEyes(parameters, config)
    val hair = parseHair(parameters, config)
    val horns = parseHorns(parameters, config)
    val mouth = parseMouth(parameters, config, character, hair)

    return Head(ears, eyes, hair, horns, mouth)
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
                parseAppearanceColor(parameters, BEARD, config, config.appearanceOptions.hair.colors),
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
    val options = config.appearanceOptions.eye

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
                    HairStyle.Bun.toString() -> Bun(
                        parseAppearanceOption(
                            parameters,
                            combine(BUN, STYLE),
                            config,
                            config.appearanceStyle.bunStyles,
                        ),
                        parse(parameters, combine(BUN, SIZE), Size.Medium),
                    )

                    HairStyle.Long.toString() -> LongHairCut(
                        parseAppearanceOption(
                            parameters,
                            combine(LONG, STYLE),
                            config,
                            config.appearanceStyle.longHairStyles,
                        ),
                        parseHairLength(parameters, config),
                    )

                    HairStyle.Ponytail.toString() -> Ponytail(
                        parseAppearanceOption(
                            parameters,
                            combine(PONYTAIL, STYLE),
                            config,
                            config.appearanceStyle.ponytailStyles,
                        ),
                        parseAppearanceOption(
                            parameters,
                            combine(PONYTAIL, POSITION),
                            config,
                            config.appearanceStyle.ponytailPositions,
                        ),
                        parseHairLength(parameters, config),
                    )

                    HairStyle.Short.toString() -> ShortHairCut(
                        parseAppearanceOption(
                            parameters,
                            combine(SHORT, STYLE),
                            config,
                            config.appearanceStyle.shortHairStyles,
                        ),
                    )

                    else -> generateHairCut(config)
                },
                parseAppearanceColor(parameters, HAIR, config, config.appearanceOptions.hair.colors),
            )
        }

        else -> generateHair(config)
    }
}

private fun parseHairLength(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
) = parseAppearanceOption(
    parameters,
    combine(HAIR, LENGTH),
    config,
    config.appearanceStyle.hairLengths,
)

private fun parseMouth(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    character: Character,
    hair: Hair,
): Mouth {
    val mouthOptions = config.appearanceOptions.mouth

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

        MouthType.Beak.toString() -> Beak(
            parseAppearanceOption(parameters, combine(BEAK, SHAPE), config, mouthOptions.beakShapes),
            parseAppearanceColor(parameters, BEAK, config, mouthOptions.beakColors),
        )

        MouthType.Snout.toString() -> Snout(
            parseAppearanceOption(parameters, combine(SNOUT, SHAPE), config, mouthOptions.snoutShapes),
            parseAppearanceColor(parameters, SNOUT, config, mouthOptions.snoutColors),
        )

        else -> generateMouth(config, hair)
    }
}
