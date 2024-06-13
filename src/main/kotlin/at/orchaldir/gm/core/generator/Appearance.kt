package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.style.HairStyleType
import at.orchaldir.gm.core.model.culture.style.StyleOptions
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.utils.NumberGenerator

data class AppearanceGeneratorConfig(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val character: Character,
    val appearanceOptions: AppearanceOptions,
    val styleOptions: StyleOptions,
) {

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    fun <T> select(list: List<T>) = numberGenerator.select(list)

}

fun generateBeard(config: AppearanceGeneratorConfig): Beard {
    val options = config.appearanceOptions

    return when (config.generate(options.beardOptions.types)) {
        BeardType.None -> NoBeard
        BeardType.Normal -> NormalBeard(
            when (config.select(BeardStyleType.entries)) {
                BeardStyleType.Goatee -> Goatee(config.select(GoateeStyle.entries))
                BeardStyleType.GoateeAndMoustache -> GoateeAndMoustache(
                    config.select(MoustacheStyle.entries),
                    config.select(GoateeStyle.entries),
                )

                BeardStyleType.Moustache -> Moustache(config.select(MoustacheStyle.entries))
                BeardStyleType.Shaved -> ShavedBeard
            },
            config.generate(options.beardOptions.colors),
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

    return Eye(
        config.generate(options.eyeShapes),
        config.generate(options.pupilShapes),
        config.generate(options.pupilColors),
        config.generate(options.scleraColors),
    )
}

fun generateHair(config: AppearanceGeneratorConfig): Hair {
    val options = config.appearanceOptions

    return when (config.generate(options.hairOptions.types)) {
        HairType.None -> NoHair
        HairType.Normal -> NormalHair(
            generateHairStyle(config),
            config.generate(options.hairOptions.colors),
        )
    }
}

fun generateHairStyle(config: AppearanceGeneratorConfig): HairStyle {
    return when (config.generate(config.styleOptions.hairStyles)) {
        HairStyleType.Afro -> Afro
        HairStyleType.BuzzCut -> BuzzCut
        HairStyleType.FlatTop -> FlatTop
        HairStyleType.MiddlePart -> MiddlePart
        HairStyleType.Shaved -> ShavedHair
        HairStyleType.SidePart -> SidePart(config.select(Side.entries))
        HairStyleType.Spiked -> Spiked
    }
}

fun generateMouth(config: AppearanceGeneratorConfig): Mouth {
    val options = config.appearanceOptions

    return when (config.generate(options.mouthTypes)) {
        MouthType.NoMouth -> NoMouth
        MouthType.SimpleMouth -> {
            if (config.character.gender == Gender.Female) {
                return FemaleMouth(
                    config.select(Size.entries),
                    config.select(Color.entries),
                    config.select(TeethColor.entries),
                )
            }
            SimpleMouth(
                generateBeard(config),
                config.select(Size.entries),
                config.select(TeethColor.entries),
            )
        }
    }
}


fun generateSkin(config: AppearanceGeneratorConfig): Skin {
    val options = config.appearanceOptions

    return when (config.generate(options.skinTypes)) {
        SkinType.Scales -> Scales(config.generate(options.scalesColors))
        SkinType.Normal -> NormalSkin(config.generate(options.normalSkinColors))
        SkinType.Exotic -> ExoticSkin(config.generate(options.exoticSkinColors))
    }
}
