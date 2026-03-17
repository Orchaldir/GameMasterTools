package at.orchaldir.gm.app.html.character.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectFromOneOf
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.generator.generateSkin
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.HairColorType
import at.orchaldir.gm.core.model.race.appearance.SkinOptions
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
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
            combine(param, EXOTIC),
            options.exoticColors,
            skin.color
        )

        is Fur -> selectHairColor(
            options.furColors,
            skin.color,
            combine(param, FUR),
        )

        is MaterialSkin -> selectFromOneOf(
            "Material",
            combine(param, MATERIAL),
            state.getMaterialStorage(),
            options.materials,
            skin.material,
        ) { material -> material.name.text }

        is NormalSkin -> selectFromOneOf(
            "Color",
            combine(param, NORMAL),
            options.normalColors,
            skin.color,
        ) { skinColor ->
            label = skinColor.name
            value = skinColor.toString()
            val bgColor = CHARACTER_CONFIG.colors.getSkinColor(skinColor).toCode()
            style = "background-color:${bgColor}"
        }

        is Scales -> selectColor(
            "Color",
            combine(param, SCALE),
            options.scalesColors,
            skin.color,
        )
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
        SkinType.Exotic.toString() -> ExoticSkin(
            parseAppearanceColor(parameters, combine(param, EXOTIC), config, options.exoticColors)
        )
        SkinType.Fur.toString() -> Fur(
            parseHairColor(parameters, config, options.furColors, combine(param, FUR)),
        )
        SkinType.Material.toString() -> MaterialSkin(
            parseMaterialId(parameters, combine(param, MATERIAL)),
        )
        SkinType.Normal.toString() -> NormalSkin(
            parseAppearanceOption(parameters, combine(param, NORMAL), config, options.normalColors),
        )

        SkinType.Scales.toString() -> Scales(
            parseAppearanceColor(parameters, combine(param, SCALE), config, options.scalesColors),
        )

        else -> generateSkin(config)
    }
}
