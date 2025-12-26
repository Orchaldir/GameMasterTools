package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.core.model.character.appearance.tail.TailsLayout
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.race.appearance.*
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
    val appearanceFashion: AppearanceFashion,
) {
    fun generate(): Appearance {
        val skin = generateSkin(this)

        return when (generate(appearanceOptions.appearanceTypes)) {
            AppearanceType.Body -> HumanoidBody(
                generateBody(this),
                generateHead(this),
                heightDistribution.center,
                skin,
                generateTails(this),
                generateWings(this),
            )

            AppearanceType.HeadOnly -> HeadOnly(
                generateHead(this),
                heightDistribution.center,
                skin
            )
        }
    }

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    fun <T> select(list: List<T>) = numberGenerator.select(list)

}

fun generateBody(config: AppearanceGeneratorConfig) = Body(
    config.select(getAvailableBodyShapes(config.gender)),
    generateFoot(config),
    config.select(Size.entries),
)

fun generateFoot(config: AppearanceGeneratorConfig): Foot {
    val options = config.appearanceOptions.foot

    return when (config.generate(options.footTypes)) {
        FootType.Normal -> NormalFoot
        FootType.Clawed -> ClawedFoot(
            options.clawNumber,
            config.generate(options.clawSizes),
            config.generate(options.clawColors),
        )
    }
}

fun generateHead(config: AppearanceGeneratorConfig): Head {
    val hair = generateHair(config)

    return Head(
        generateEars(config),
        generateEyes(config),
        hair,
        generateHorns(config),
        generateMouth(config, hair),
    )
}

fun generateBeard(config: AppearanceGeneratorConfig, hair: Hair): Beard {
    val options = config.appearanceOptions
    val fashion = config.appearanceFashion.beard

    return when (config.generate(options.hair.beardTypes)) {
        BeardType.None -> NoBeard
        BeardType.Normal -> NormalBeard(
            when (config.generate(fashion.beardStyles)) {
                BeardStyleType.Full -> FullBeard(
                    config.generate(fashion.fullBeardStyles),
                    config.generate(fashion.beardLength),
                )

                BeardStyleType.Goatee -> Goatee(config.generate(fashion.goateeStyles))
                BeardStyleType.GoateeAndMoustache -> GoateeAndMoustache(
                    config.generate(fashion.moustacheStyles),
                    config.generate(fashion.goateeStyles),
                )

                BeardStyleType.Moustache -> Moustache(config.generate(fashion.moustacheStyles))
                BeardStyleType.Shaved -> ShavedBeard
            },
            when (hair) {
                NoHair -> config.generate(options.hair.colors)
                is ExoticHair -> hair.color
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
    val options = config.appearanceOptions.eye

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

    return when (config.generate(options.hair.hairTypes)) {
        HairType.None -> NoHair
        HairType.Exotic -> ExoticHair(
            generateHairCut(config),
            config.generate(options.hair.colors),
        )
    }
}

fun generateHairCut(config: AppearanceGeneratorConfig): HairCut {
    val fashion = config.appearanceFashion.hair

    return when (config.generate(fashion.hairStyles)) {
        HairStyle.Bun -> Bun(
            config.generate(fashion.bunStyles),
            config.select(Size.entries),
        )

        HairStyle.Long -> LongHairCut(
            config.generate(fashion.longHairStyles),
            config.generate(fashion.hairLengths),
        )

        HairStyle.Short -> ShortHairCut(config.generate(fashion.shortHairStyles))
        HairStyle.Ponytail -> Ponytail(
            config.generate(fashion.ponytailStyles),
            config.generate(fashion.ponytailPositions),
            config.generate(fashion.hairLengths),
        )
    }
}

fun generateHorns(config: AppearanceGeneratorConfig): Horns {
    val options = config.appearanceOptions.horn

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
            generateFeatureColor(config, options.colors),
        )
    }
}

fun generateHorn(config: AppearanceGeneratorConfig, options: HornOptions): SimpleHorn {
    val type = config.generate(options.simpleTypes)

    return SimpleHorn(
        options.getSimpleLength(type),
        type,
        generateFeatureColor(config, options.colors),
    )
}

fun generateMouth(config: AppearanceGeneratorConfig, hair: Hair): Mouth {
    val options = config.appearanceOptions.mouth

    return when (config.generate(options.mouthTypes)) {
        MouthType.NoMouth -> NoMouth
        MouthType.NormalMouth -> {
            if (config.gender == Gender.Female) {
                return FemaleMouth(
                    config.select(Size.entries),
                    config.generate(config.appearanceFashion.lipColors),
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

fun generateSkin(config: AppearanceGeneratorConfig) = generateSkin(config, config.appearanceOptions.skin)

fun generateSkin(config: AppearanceGeneratorConfig, options: SkinOptions) = when (config.generate(options.skinTypes)) {
    SkinType.Exotic -> ExoticSkin(config.generate(options.exoticColors))
    SkinType.Fur -> Fur(config.generate(options.furColors))
    SkinType.Material -> MaterialSkin(config.generate(options.materials))
    SkinType.Normal -> NormalSkin(config.generate(options.normalColors))
    SkinType.Scales -> Scales(config.generate(options.scalesColors))
}

fun generateTails(config: AppearanceGeneratorConfig): Tails {
    val options = config.appearanceOptions.tail

    return when (config.generate(options.layouts)) {
        TailsLayout.None -> NoTails
        TailsLayout.Simple -> generateSimpleTail(config, options)
    }
}

private fun generateSimpleTail(
    config: AppearanceGeneratorConfig,
    tailOptions: TailOptions,
): SimpleTail {
    val shape = config.generate(tailOptions.simpleShapes)
    val options = tailOptions.getFeatureColorOptions(shape)

    return SimpleTail(
        shape,
        config.select(Size.entries),
        generateFeatureColor(config, options),
    )
}

private fun generateFeatureColor(
    config: AppearanceGeneratorConfig,
    options: FeatureColorOptions,
) = when (options.types) {
    FeatureColorType.Hair -> ReuseHairColor
    FeatureColorType.Overwrite -> OverwriteFeatureColor(generateSkin(config, options.skin))
    FeatureColorType.Skin -> ReuseSkinColor
}

fun generateWings(config: AppearanceGeneratorConfig): Wings {
    val options = config.appearanceOptions.wing

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
    val options = config.appearanceOptions.wing

    return when (config.generate(options.types)) {
        WingType.Bat -> BatWing(generateFeatureColor(config, options.batColors))
        WingType.Bird -> BirdWing(config.generate(options.birdColors))
        WingType.Butterfly -> ButterflyWing(config.generate(options.butterflyColors))
    }
}
