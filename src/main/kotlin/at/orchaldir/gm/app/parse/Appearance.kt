package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.core.generator.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.getRaceAppearance
import at.orchaldir.gm.utils.RandomNumberGenerator
import io.ktor.http.*
import kotlin.random.Random

fun createGenerationConfig(state: State, character: Character): AppearanceGeneratorConfig {
    val culture = state.getCultureStorage().getOrThrow(character.culture)
    val race = state.getRaceStorage().getOrThrow(character.race)

    return AppearanceGeneratorConfig(
        RandomNumberGenerator(Random),
        state.rarityGenerator,
        character.gender,
        race.height,
        state.getRaceAppearance(character),
        culture.appearanceStyle
    )
}

fun generateAppearance(
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val type = config.generate(config.appearanceOptions.appearanceTypes)
    val parameters = parametersOf(APPEARANCE, type.toString())

    return parseAppearance(parameters, config, character)
}

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
    parse(parameters, SKIN_EXOTIC_COLOR, Color.Red)
