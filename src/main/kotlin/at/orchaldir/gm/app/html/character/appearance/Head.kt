package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
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
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.culture.fashion.BeardFashion
import at.orchaldir.gm.core.model.culture.fashion.HairFashion
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// edit

fun HtmlBlockTag.editHead(
    state: State,
    raceAppearance: RaceAppearance,
    fashion: AppearanceFashion?,
    head: Head,
) {
    editEars(raceAppearance, head.ears)
    editEyes(raceAppearance, head.eyes)
    editHair(raceAppearance, fashion, head.hair)
    editHorns(state, raceAppearance, head.horns)
    editMouth(raceAppearance, fashion, head.mouth)
}

private fun HtmlBlockTag.editEars(raceAppearance: RaceAppearance, ears: Ears) {
    h2 { +"Ears" }

    selectFromOneOf("Type", combine(EAR, TYPE), raceAppearance.earsLayout, ears.getType())

    when (ears) {
        is NormalEars -> {
            selectFromOneOf("Ear Shape", combine(EAR, SHAPE), raceAppearance.earShapes, ears.shape)
            selectValue("Ear Size", combine(EAR, SIZE), Size.entries, ears.size)
        }

        else -> doNothing()
    }
}

private fun HtmlBlockTag.editBeard(
    raceAppearance: RaceAppearance,
    fashion: BeardFashion?,
    beard: Beard,
) {
    h2 { +"Beard" }

    selectFromOneOf("Type", BEARD, raceAppearance.hair.beardTypes, beard.getType())

    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> editNormalBeard(raceAppearance, fashion, beard)
    }
}

private fun HtmlBlockTag.editNormalBeard(
    raceAppearance: RaceAppearance,
    fashion: BeardFashion?,
    beard: NormalBeard,
) {
    selectFromOptionalOneOf(
        "Style",
        combine(BEARD, STYLE),
        fashion?.beardStyles,
        beard.style.getType(),
    )
    selectColor("Color", combine(BEARD, COLOR), raceAppearance.hair.colors, beard.color)

    when (val style = beard.style) {
        is FullBeard -> {
            selectFromOptionalOneOf(
                "Full Beard",
                combine(FULL, STYLE),
                fashion?.fullBeardStyles,
                style.style,
            )
            selectFromOptionalOneOf(
                "Beard Length",
                combine(BEARD, LENGTH),
                fashion?.beardLength,
                style.length,
            )
        }

        is Goatee -> selectGoateeStyle(fashion, style.goateeStyle)
        is GoateeAndMoustache -> {
            selectGoateeStyle(fashion, style.goateeStyle)
            selectMoustacheStyle(fashion, style.moustacheStyle)
        }

        is Moustache -> selectMoustacheStyle(fashion, style.moustacheStyle)
        ShavedBeard -> doNothing()
    }
}

private fun HtmlBlockTag.selectGoateeStyle(
    fashion: BeardFashion?,
    current: GoateeStyle,
) {
    selectFromOptionalOneOf("Goatee", combine(GOATEE, STYLE), fashion?.goateeStyles, current)
}

private fun HtmlBlockTag.selectMoustacheStyle(
    fashion: BeardFashion?,
    current: MoustacheStyle,
) {
    selectFromOptionalOneOf("Moustache", combine(MOUSTACHE, STYLE), fashion?.moustacheStyles, current)
}

private fun HtmlBlockTag.editEyes(
    raceAppearance: RaceAppearance,
    eyes: Eyes,
) {
    h2 { +"Eyes" }

    selectFromOneOf("Layout", combine(EYE, LAYOUT), raceAppearance.eyesLayout, eyes.getType())

    when (eyes) {
        is OneEye -> {
            editEye(raceAppearance.eye, eyes.eye)
            selectValue("Eye Size", combine(EYE, SIZE), Size.entries, eyes.size)
        }

        is TwoEyes -> {
            editEye(raceAppearance.eye, eyes.eye)
        }

        else -> doNothing()
    }
}

private fun HtmlBlockTag.editEye(
    eyeOptions: EyeOptions,
    eye: Eye,
) {
    selectFromOneOf("Eye Type", combine(EYE, TYPE), eyeOptions.eyeTypes, eye.getType())

    when (eye) {
        is NormalEye -> editNormalEye(eyeOptions, eye)

        is SimpleEye -> {
            selectFromOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape) { shape ->
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
        selectFromOneOf("Eye Shape", combine(EYE, SHAPE), eyeOptions.eyeShapes, eye.eyeShape)
        selectFromOneOf("Pupil Shape", combine(PUPIL, SHAPE), eyeOptions.pupilShapes, eye.pupilShape)
        selectColor("Eye Color", combine(PUPIL, COLOR), eyeOptions.eyeColors, eye.pupilColor)
        selectColor("Sclera Color", combine(PUPIL, SCLERA), eyeOptions.scleraColors, eye.scleraColor)
    }
}

private fun HtmlBlockTag.editHair(
    raceAppearance: RaceAppearance,
    fashion: AppearanceFashion?,
    hair: Hair,
) {
    h2 { +"Hair" }

    selectFromOneOf("Type", HAIR, raceAppearance.hair.hairTypes, hair.getType())

    when (hair) {
        NoHair -> doNothing()
        is NormalHair -> editNormalHair(raceAppearance, fashion?.hair, hair)
    }
}

private fun HtmlBlockTag.editNormalHair(
    raceAppearance: RaceAppearance,
    fashion: HairFashion?,
    hair: NormalHair,
) {
    selectFromOptionalOneOf(
        "Haircut",
        combine(HAIR, STYLE),
        fashion?.hairStyles,
        hair.cut.getType(),
    )
    selectColor("Color", combine(HAIR, COLOR), raceAppearance.hair.colors, hair.color)

    when (val cut = hair.cut) {
        is Bun -> {
            selectFromOptionalOneOf(
                "Bun Style",
                combine(BUN, STYLE),
                fashion?.bunStyles,
                cut.style,
            )
            selectValue("Bun Size", combine(BUN, SIZE), Size.entries, cut.size)
        }

        is LongHairCut -> {
            selectFromOptionalOneOf(
                "Long Hair Style",
                combine(LONG, STYLE),
                fashion?.longHairStyles,
                cut.style,
            )
            selectHairLength(fashion, cut.length)
        }

        is Ponytail -> {
            selectFromOptionalOneOf(
                "Ponytail Style",
                combine(PONYTAIL, STYLE),
                fashion?.ponytailStyles,
                cut.style,
            )
            selectFromOptionalOneOf(
                "Ponytail Position",
                combine(PONYTAIL, POSITION),
                fashion?.ponytailPositions,
                cut.position,
            )
            selectHairLength(fashion, cut.length)
        }

        is ShortHairCut -> selectFromOptionalOneOf(
            "Short Hair Style",
            combine(SHORT, STYLE),
            fashion?.shortHairStyles,
            cut.style,
        )
    }
}

private fun HtmlBlockTag.selectHairLength(
    fashion: HairFashion?,
    length: HairLength,
) {
    selectFromOptionalOneOf(
        "Length",
        combine(HAIR, LENGTH),
        fashion?.hairLengths,
        length,
    )
}

private fun HtmlBlockTag.editMouth(
    raceAppearance: RaceAppearance,
    fashion: AppearanceFashion?,
    mouth: Mouth,
) {
    h2 { +"Mouth" }
    val mouthOptions = raceAppearance.mouth

    selectFromOneOf("Type", combine(MOUTH, TYPE), mouthOptions.mouthTypes, mouth.getType())

    when (mouth) {
        NoMouth -> doNothing()

        is NormalMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            editBeard(raceAppearance, fashion?.beard, mouth.beard)
        }

        is FemaleMouth -> {
            editSimpleMouth(mouth.width, mouth.teethColor)
            val colors = fashion?.lipColors ?: OneOf(Color.entries)
            selectColor("Lip Color", combine(LIP, COLOR), colors, mouth.color)
        }

        is Beak -> {
            selectFromOneOf("Beak Shape", combine(BEAK, SHAPE), mouthOptions.beakShapes, mouth.shape)
            selectFromOneOf("Beak Color", combine(BEAK, COLOR), mouthOptions.beakColors, mouth.color)
        }

        is Snout -> {
            selectFromOneOf("Snout Shape", combine(SNOUT, SHAPE), mouthOptions.snoutShapes, mouth.shape)
            selectFromOneOf("Snout Color", combine(SNOUT, COLOR), mouthOptions.snoutColors, mouth.color)
        }
    }
}

private fun HtmlBlockTag.editSimpleMouth(size: Size, teethColor: TeethColor) {
    selectValue("Width", combine(MOUTH, WIDTH), Size.entries, size)
    selectValue("Teeth Color", TEETH_COLOR, TeethColor.entries, teethColor)
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
    val fashion = config.appearanceFashion.beard

    return when (parameters[BEARD]) {
        BeardType.None.toString() -> NoBeard
        BeardType.Normal.toString() -> parseNormalBeard(parameters, config, fashion)
        else -> generateBeard(config, hair)
    }
}

private fun parseNormalBeard(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    fashion: BeardFashion,
) = NormalBeard(
    when (parameters[combine(BEARD, STYLE)]) {
        BeardStyleType.Full.toString() -> FullBeard(
            parseAppearanceOption(parameters, combine(FULL, STYLE), config, fashion.fullBeardStyles),
            parseAppearanceOption(parameters, combine(BEARD, LENGTH), config, fashion.beardLength),
        )

        BeardStyleType.Goatee.toString() -> Goatee(
            parseGoateeStyle(parameters, config, fashion),
        )

        BeardStyleType.GoateeAndMoustache.toString() -> GoateeAndMoustache(
            parseMoustacheStyle(parameters, config, fashion),
            parseGoateeStyle(parameters, config, fashion),
        )

        BeardStyleType.Moustache.toString() -> Moustache(
            parseMoustacheStyle(parameters, config, fashion),
        )

        BeardStyleType.Shaved.toString() -> ShavedBeard

        else -> Goatee(GoateeStyle.Goatee)
    },
    parseAppearanceColor(parameters, BEARD, config, config.appearanceOptions.hair.colors),
)

private fun parseMoustacheStyle(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    fashion: BeardFashion,
) = parseAppearanceOption(parameters, combine(MOUSTACHE, STYLE), config, fashion.moustacheStyles)

private fun parseGoateeStyle(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    fashion: BeardFashion,
) = parseAppearanceOption(parameters, combine(GOATEE, STYLE), config, fashion.goateeStyles)

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
    val fashion = config.appearanceFashion.hair

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
                            fashion.bunStyles,
                        ),
                        parse(parameters, combine(BUN, SIZE), Size.Medium),
                    )

                    HairStyle.Long.toString() -> LongHairCut(
                        parseAppearanceOption(
                            parameters,
                            combine(LONG, STYLE),
                            config,
                            fashion.longHairStyles,
                        ),
                        parseHairLength(parameters, config),
                    )

                    HairStyle.Ponytail.toString() -> Ponytail(
                        parseAppearanceOption(
                            parameters,
                            combine(PONYTAIL, STYLE),
                            config,
                            fashion.ponytailStyles,
                        ),
                        parseAppearanceOption(
                            parameters,
                            combine(PONYTAIL, POSITION),
                            config,
                            fashion.ponytailPositions,
                        ),
                        parseHairLength(parameters, config),
                    )

                    HairStyle.Short.toString() -> ShortHairCut(
                        parseAppearanceOption(
                            parameters,
                            combine(SHORT, STYLE),
                            config,
                            fashion.shortHairStyles,
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
    config.appearanceFashion.hair.hairLengths,
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
                    parseAppearanceColor(parameters, LIP, config, config.appearanceFashion.lipColors),
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
