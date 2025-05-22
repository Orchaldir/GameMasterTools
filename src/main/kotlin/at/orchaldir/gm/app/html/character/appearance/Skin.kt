package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectFromOneOf
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateSkin
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.race.appearance.SkinOptions
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.style

// edit

fun HtmlBlockTag.editSkin(
    state: State,
    options: SkinOptions,
    skin: Skin,
) {
    h2 { +"Skin" }

    editSkinInternal(state, options, skin, SKIN)
}

fun HtmlBlockTag.editSkin(
    state: State,
    options: SkinOptions,
    skin: Skin,
    param: String,
) {
    showDetails("Skin") {
        editSkinInternal(state, options, skin, param)
    }
}

private fun HtmlBlockTag.editSkinInternal(
    state: State,
    options: SkinOptions,
    skin: Skin,
    param: String,
) {
    selectFromOneOf("Type", combine(param, TYPE), options.skinTypes, skin.getType())

    when (skin) {
        is ExoticSkin -> selectColor(
            "Color",
            combine(param, EXOTIC, COLOR),
            options.exoticColors,
            skin.color
        )

        is Fur -> selectColor("Color", combine(param, EXOTIC, COLOR), options.furColors, skin.color)

        is MaterialSkin -> selectFromOneOf(
            "Material",
            combine(param, MATERIAL),
            state.getMaterialStorage(),
            options.materials,
            skin.material,
        ) { material -> material.name.text }

        is NormalSkin -> {
            selectFromOneOf(
                "Color",
                combine(param, COLOR),
                options.normalColors,
                skin.color,
            ) { skinColor ->
                label = skinColor.name
                value = skinColor.toString()
                val bgColor = CHARACTER_CONFIG.getSkinColor(skinColor).toCode()
                style = "background-color:${bgColor}"
            }
        }

        is Scales -> selectColor("Color", combine(param, EXOTIC, COLOR), options.scalesColors, skin.color)
    }
}

// parse

fun parseSkin(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    param: String = SKIN,
) = parseSkin(parameters, config, config.appearanceOptions.skin, param)

fun parseSkin(
    parameters: Parameters,
    config: AppearanceGeneratorConfig,
    options: SkinOptions,
    param: String = SKIN,
): Skin {

    return when (parameters[combine(param, TYPE)]) {
        SkinType.Exotic.toString() -> {
            return ExoticSkin(parseExoticColor(parameters, config, options.exoticColors, param))
        }

        SkinType.Fur.toString() -> {
            return Fur(parseExoticColor(parameters, config, options.furColors, param))
        }

        SkinType.Material.toString() -> MaterialSkin(parseMaterialId(parameters, combine(param, MATERIAL)))

        SkinType.Normal.toString() -> {
            val color = parseAppearanceOption(parameters, combine(param, COLOR), config, options.normalColors)
            return NormalSkin(color)
        }

        SkinType.Scales.toString() -> {
            return Scales(parseExoticColor(parameters, config, options.scalesColors, param))
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
