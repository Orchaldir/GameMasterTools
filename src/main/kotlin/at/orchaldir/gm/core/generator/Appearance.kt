package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.race.appearance.DEFAULT_CROWN_WIDTH
import at.orchaldir.gm.core.model.race.appearance.HornOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution

data class AppearanceGeneratorConfig(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val gender: Gender,
    val heightDistribution: Distribution<Distance>,
    val appearanceOptions: RaceAppearance,
    val appearanceStyle: AppearanceStyle,
) {
    fun generate(): Appearance {
        val skin = generateSkin(this)

        return when (generate(appearanceOptions.appearanceTypes)) {
            AppearanceType.Body -> HumanoidBody(
                generateBody(this, skin),
                generateHead(this, skin),
                heightDistribution.center,
                generateWings(this),
            )

            AppearanceType.HeadOnly -> HeadOnly(
                generateHead(this, skin),
                heightDistribution.center,
            )
        }
    }

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    fun <T> select(list: List<T>) = numberGenerator.select(list)

}

fun generateBody(config: AppearanceGeneratorConfig, skin: Skin) = Body(
    config.select(getAvailableBodyShapes(config.gender)),
    generateFoot(config),
    config.select(Size.entries),
    skin,
)

fun generateFoot(config: AppearanceGeneratorConfig): Foot {
    val options = config.appearanceOptions.footOptions

    return when (config.generate(options.footTypes)) {
        FootType.Normal -> NormalFoot
        FootType.Clawed -> ClawedFoot(
            options.clawNumber,
            config.generate(options.clawSizes),
            config.generate(options.clawColors),
        )
    }
}

fun generateHead(config: AppearanceGeneratorConfig, skin: Skin): Head {
    val hair = generateHair(config)

    return Head(
        generateEars(config),
        generateEyes(config),
        hair,
        generateHorns(config),
        generateMouth(config, hair),
        skin,
    )
}

fun generateBeard(config: AppearanceGeneratorConfig, hair: Hair): Beard {
    val options = config.appearanceOptions
    val styleOptions = config.appearanceStyle

    return when (config.generate(options.hairOptions.beardTypes)) {
        BeardType.None -> NoBeard
        BeardType.Normal -> NormalBeard(
            when (config.generate(styleOptions.beardStyles)) {
                BeardStyleType.Goatee -> Goatee(config.generate(styleOptions.goateeStyles))
                BeardStyleType.GoateeAndMoustache -> GoateeAndMoustache(
                    config.generate(styleOptions.moustacheStyles),
                    config.generate(styleOptions.goateeStyles),
                )

                BeardStyleType.Moustache -> Moustache(config.generate(styleOptions.moustacheStyles))
                BeardStyleType.Shaved -> ShavedBeard
            },
            when (hair) {
                NoHair -> config.generate(options.hairOptions.colors)
                is NormalHair -> hair.color
            }
        )
    }
}

fun generateEars(config: AppearanceGeneratorConfig): Ears {
    val options = config.appearanceOptions

    return when (config.generate(options.earsLayout)) {
        EarsLayout.NoEars -> NoEars
        EarsLayout.NormalEars -> NormalEars(
            config.generate(options.earShapes),
            config.select(Size.entries),
        )
    }
}

fun generateEyes(config: AppearanceGeneratorConfig): Eyes {
    val options = config.appearanceOptions

    return when (config.generate(options.eyesLayout)) {
        EyesLayout.NoEyes -> NoEyes
        EyesLayout.OneEye -> OneEye(
            generateEye(config),
            config.select(Size.entries),
        )

        EyesLayout.TwoEyes -> TwoEyes(generateEye(config))
    }
}

fun generateEye(config: AppearanceGeneratorConfig): Eye {
    val options = config.appearanceOptions.eyeOptions

    return when (config.generate(options.eyeTypes)) {
        EyeType.Simple -> SimpleEye(
            config.generate(options.eyeShapes),
            config.generate(options.eyeColors),
        )

        EyeType.Normal -> NormalEye(
            config.generate(options.eyeShapes),
            config.generate(options.pupilShapes),
            config.generate(options.eyeColors),
            config.generate(options.scleraColors),
        )
    }
}

fun generateHair(config: AppearanceGeneratorConfig): Hair {
    val options = config.appearanceOptions

    return when (config.generate(options.hairOptions.hairTypes)) {
        HairType.None -> NoHair
        HairType.Normal -> NormalHair(
            generateHairStyle(config),
            config.generate(options.hairOptions.colors),
        )
    }
}

fun generateHairStyle(config: AppearanceGeneratorConfig): HairStyle {
    return when (config.generate(config.appearanceStyle.hairStyles)) {
        HairStyleType.BuzzCut -> BuzzCut
        HairStyleType.FlatTop -> FlatTop
        HairStyleType.MiddlePart -> MiddlePart
        HairStyleType.Shaved -> ShavedHair
        HairStyleType.SidePart -> SidePart(config.select(Side.entries))
        HairStyleType.Spiked -> Spiked
    }
}

fun generateHorns(config: AppearanceGeneratorConfig): Horns {
    val options = config.appearanceOptions.hornOptions

    return when (config.generate(options.layouts)) {

        HornsLayout.None -> NoHorns
        HornsLayout.Two -> TwoHorns(generateHorn(config, options))
        HornsLayout.Different -> DifferentHorns(
            generateHorn(config, options),
            generateHorn(config, options),
        )

        HornsLayout.Crown -> CrownOfHorns(
            config.generate(options.crownFront),
            config.generate(options.crownBack),
            true,
            options.crownLength,
            DEFAULT_CROWN_WIDTH,
            config.generate(options.colors),
        )
    }
}

fun generateHorn(config: AppearanceGeneratorConfig, options: HornOptions): SimpleHorn {
    val type = config.generate(options.simpleTypes)

    return SimpleHorn(
        options.getSimpleLength(type),
        type,
        config.generate(options.colors),
    )
}

fun generateMouth(config: AppearanceGeneratorConfig, hair: Hair): Mouth {
    val options = config.appearanceOptions.mouthOptions

    return when (config.generate(options.mouthTypes)) {
        MouthType.NoMouth -> NoMouth
        MouthType.NormalMouth -> {
            if (config.gender == Gender.Female) {
                return FemaleMouth(
                    config.select(Size.entries),
                    config.generate(config.appearanceStyle.lipColors),
                    TeethColor.White,
                )
            }
            NormalMouth(
                generateBeard(config, hair),
                config.select(Size.entries),
                TeethColor.White,
            )
        }

        MouthType.Beak -> Beak(
            config.generate(options.beakShapes),
            config.generate(options.beakColors),
        )

        MouthType.Snout -> Snout(
            config.generate(options.snoutShapes),
            config.generate(options.snoutColors),
        )
    }
}


fun generateSkin(config: AppearanceGeneratorConfig): Skin {
    val options = config.appearanceOptions

    return when (config.generate(options.skinTypes)) {
        SkinType.Fur -> Fur(config.generate(options.furColors))
        SkinType.Scales -> Scales(config.generate(options.scalesColors))
        SkinType.Normal -> NormalSkin(config.generate(options.normalSkinColors))
        SkinType.Exotic -> ExoticSkin(config.generate(options.exoticSkinColors))
    }
}

fun generateWings(config: AppearanceGeneratorConfig): Wings {
    val options = config.appearanceOptions.wingOptions

    return when (config.generate(options.layouts)) {
        WingsLayout.None -> NoWings
        WingsLayout.One -> OneWing(
            generateWing(config),
            Side.Right,
        )

        WingsLayout.Two -> TwoWings(generateWing(config))
        WingsLayout.Different -> DifferentWings(
            generateWing(config),
            generateWing(config),
        )
    }
}

fun generateWing(config: AppearanceGeneratorConfig): Wing {
    val options = config.appearanceOptions.wingOptions

    return when (config.generate(options.types)) {
        WingType.Bat -> BatWing(config.generate(options.batColors))
        WingType.Bird -> BirdWing(config.generate(options.birdColors))
        WingType.Butterfly -> ButterflyWing(config.generate(options.butterflyColors))
    }
}
