package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
import at.orchaldir.gm.core.model.race.appearance.EyesLayout
import at.orchaldir.gm.core.model.race.appearance.MouthType
import at.orchaldir.gm.core.model.race.appearance.SkinType
import at.orchaldir.gm.utils.NumberGenerator

data class AppearanceGeneratorConfig(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val options: AppearanceOptions,
) {

    fun <T> generate(map: RarityMap<T>) = rarityGenerator.generate(map, numberGenerator)

}

fun generateEyes(config: AppearanceGeneratorConfig): Eyes {
    val generator = config.rarityGenerator
    val numbers = config.numberGenerator
    val options = config.options

    return when (generator.generate(options.eyesLayout, numbers)) {
        EyesLayout.NoEyes -> NoEyes
        EyesLayout.OneEye -> OneEye(generateEye(config))
        EyesLayout.TwoEyes -> TwoEyes(generateEye(config))
    }
}

fun generateEye(config: AppearanceGeneratorConfig): Eye {
    val generator = config.rarityGenerator
    val numbers = config.numberGenerator
    val options = config.options.eyeOptions

    return Eye(
        generator.generate(options.eyeShapes, numbers),
        generator.generate(options.pupilShapes, numbers),
        generator.generate(options.pupilColors, numbers),
        generator.generate(options.scleraColors, numbers),
    )
}

fun generateMouth(config: AppearanceGeneratorConfig): Mouth {
    val generator = config.rarityGenerator
    val numbers = config.numberGenerator
    val options = config.options

    return when (generator.generate(options.mouthTypes, numbers)) {
        MouthType.NoMouth -> NoMouth
        MouthType.SimpleMouth -> SimpleMouth(
            numbers.select(Size.entries),
            numbers.select(TeethColor.entries),
        )

        MouthType.FemaleMouth -> FemaleMouth(
            numbers.select(Size.entries),
            numbers.select(Color.entries),
            numbers.select(TeethColor.entries),
        )
    }
}


fun generateSkin(config: AppearanceGeneratorConfig): Skin {
    val generator = config.rarityGenerator
    val numbers = config.numberGenerator
    val options = config.options

    return when (generator.generate(options.skinTypes, numbers)) {
        SkinType.Scales -> Scales(generator.generate(options.scalesColors, numbers))
        SkinType.Normal -> NormalSkin(generator.generate(options.normalSkinColors, numbers))
        SkinType.Exotic -> ExoticSkin(generator.generate(options.exoticSkinColors, numbers))
    }
}
