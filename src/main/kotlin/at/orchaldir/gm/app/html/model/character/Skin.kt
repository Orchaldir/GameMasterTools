package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.EXOTIC
import at.orchaldir.gm.app.SKIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectOneOf
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateSkin
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.h2
import kotlinx.html.style

// edit

fun FORM.editSkin(
    raceAppearance: RaceAppearance,
    skin: Skin,
) {
    h2 { +"Skin" }

    selectOneOf("Type", combine(SKIN, TYPE), raceAppearance.skinTypes, skin.getType(), true)

    when (skin) {
        is Fur -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.furColors, skin.color)
        is Scales -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.scalesColors, skin.color)
        is ExoticSkin -> selectColor("Color", combine(SKIN, EXOTIC, COLOR), raceAppearance.exoticSkinColors, skin.color)
        is NormalSkin -> {
            selectOneOf("Color", combine(SKIN, COLOR), raceAppearance.normalSkinColors, skin.color, true) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

// parse

fun parseSkin(parameters: Parameters, config: AppearanceGeneratorConfig): Skin {
    val options = config.appearanceOptions

    return when (parameters[combine(SKIN, TYPE)]) {
        SkinType.Fur.toString() -> {
            return Fur(parseExoticColor(parameters, config, options.furColors))
        }

        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters, config, options.scalesColors))
        }

        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters, config, options.exoticSkinColors))
        }

        SkinType.Normal.toString() -> {
            val color = parseAppearanceOption(parameters, combine(SKIN, COLOR), config, options.normalSkinColors)
            return NormalSkin(color)
        }

        else -> generateSkin(config)
    }
}

private fun parseExoticColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    colors: OneOf<Color>,
) = parseAppearanceColor(parameters, combine(SKIN, EXOTIC), config, colors)
