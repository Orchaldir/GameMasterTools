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
import at.orchaldir.gm.core.model.race.appearance.SkinOptions
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.h2
import kotlinx.html.style

// edit

fun FORM.editSkin(
    options: SkinOptions,
    skin: Skin,
    param: String = SKIN,
) {
    h2 { +"Skin" }

    selectOneOf("Type", combine(param, TYPE), options.skinTypes, skin.getType(), true)

    when (skin) {
        is Fur -> selectColor("Color", combine(param, EXOTIC, COLOR), options.furColors, skin.color)
        is Scales -> selectColor("Color", combine(param, EXOTIC, COLOR), options.scalesColors, skin.color)
        is ExoticSkin -> selectColor(
            "Color",
            combine(param, EXOTIC, COLOR),
            options.exoticSkinColors,
            skin.color
        )
        is NormalSkin -> {
            selectOneOf(
                "Color",
                combine(param, COLOR),
                options.normalSkinColors,
                skin.color,
                true
            ) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }
    }
}

// parse

fun parseSkin(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    param: String = SKIN,
): Skin {
    val options = config.appearanceOptions.skin

    return when (parameters[combine(param, TYPE)]) {
        SkinType.Fur.toString() -> {
            return Fur(parseExoticColor(parameters, config, options.furColors, param))
        }

        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters, config, options.scalesColors, param))
        }

        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters, config, options.exoticSkinColors, param))
        }

        SkinType.Normal.toString() -> {
            val color = parseAppearanceOption(parameters, combine(param, COLOR), config, options.normalSkinColors)
            return NormalSkin(color)
        }

        else -> generateSkin(config)
    }
}

private fun parseExoticColor(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    colors: OneOf<Color>,
    param: String,
) = parseAppearanceColor(parameters, combine(param, EXOTIC), config, colors)
