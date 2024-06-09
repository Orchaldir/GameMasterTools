package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.culture.style.HairStyleType
import at.orchaldir.gm.core.model.race.appearance.*
import at.orchaldir.gm.utils.NumberGenerator

data class AppearanceGeneratorConfig(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val options: AppearanceOptions,
) {

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

    fun <T> select(list: List<T>) = numberGenerator.select(list)

}

fun generateEars(config: AppearanceGeneratorConfig): Ears {
    val options = config.options

    return when (config.generate(options.earsLayout)) {
        EarsLayout.NoEars -> NoEars
        EarsLayout.NormalEars -> NormalEars(
            config.generate(options.earShapes),
            config.select(Size.entries),
        )
    }
}

fun generateEyes(config: AppearanceGeneratorConfig): Eyes {
    val options = config.options

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
    val options = config.options.eyeOptions

    return Eye(
        config.generate(options.eyeShapes),
        config.generate(options.pupilShapes),
        config.generate(options.pupilColors),
        config.generate(options.scleraColors),
    )
}

fun generateHair(config: AppearanceGeneratorConfig): Hair {
    val options = config.options

    return when (config.generate(options.hairOptions.types)) {
        HairType.None -> NoHair
        HairType.Normal -> NormalHair(
            generateHairStyle(config),
            config.generate(options.hairOptions.colors),
        )

        HairType.Fire -> FireHair(Size.Medium)
    }
}

fun generateHairStyle(config: AppearanceGeneratorConfig): HairStyle {
    return when (config.select(HairStyleType.entries)) {
        HairStyleType.Afro -> Afro
        HairStyleType.BuzzCut -> BuzzCut
        HairStyleType.FlatTop -> FlatTop
        HairStyleType.MiddlePart -> MiddlePart
        HairStyleType.Shaved -> Shaved
        HairStyleType.SidePart -> SidePart(config.select(Side.entries))
        HairStyleType.Spiked -> Spiked
    }
}


fun generateMouth(config: AppearanceGeneratorConfig): Mouth {
    val options = config.options

    return when (config.generate(options.mouthTypes)) {
        MouthType.NoMouth -> NoMouth
        MouthType.SimpleMouth -> SimpleMouth(
            config.select(Size.entries),
            config.select(TeethColor.entries),
        )

        MouthType.FemaleMouth -> FemaleMouth(
            config.select(Size.entries),
            config.select(Color.entries),
            config.select(TeethColor.entries),
        )
    }
}


fun generateSkin(config: AppearanceGeneratorConfig): Skin {
    val options = config.options

    return when (config.generate(options.skinTypes)) {
        SkinType.Scales -> Scales(config.generate(options.scalesColors))
        SkinType.Normal -> NormalSkin(config.generate(options.normalSkinColors))
        SkinType.Exotic -> ExoticSkin(config.generate(options.exoticSkinColors))
    }
}
