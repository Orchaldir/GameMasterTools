package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.NormalSkin
import at.orchaldir.gm.core.model.character.appearance.Scales
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
import at.orchaldir.gm.core.model.race.appearance.SkinType
import at.orchaldir.gm.utils.NumberGenerator

data class AppearanceGeneratorConfig(
    val numberGenerator: NumberGenerator,
    val rarityGenerator: RarityGenerator,
    val options: AppearanceOptions,
)


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
