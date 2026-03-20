package at.orchaldir.gm.app.html.race.appearance

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.economy.material.selectMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.race.appearance.DEFAULT_EXOTIC_COLOR
import at.orchaldir.gm.core.model.race.appearance.DEFAULT_SCALE_COLOR
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.SkinOptions
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

// show

fun HtmlBlockTag.showSkin(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    h3 { +"Skin" }

    val options = appearance.skin

    showSkinInternal(call, state, options)
}

fun HtmlBlockTag.showSkinInternal(
    call: ApplicationCall,
    state: State,
    options: SkinOptions,
) {
    showRarityMap("Type", options.skinTypes)

    if (options.skinTypes.isAvailable(SkinType.Exotic)) {
        showColorRarityMap("Exotic Skin Colors", options.exoticColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Normal)) {
        showSkinColorRarityMap(CHARACTER_CONFIG, "Normal Skin Colors", options.normalColors)
    }

    if (options.skinTypes.isAvailable(SkinType.Material)) {
        showRarityMap("Materials", options.materials) { id ->
            link(call, state, id)
        }
    }

    if (options.skinTypes.isAvailable(SkinType.Fur)) {
        optionalFieldLink("Fur", call, state, options.fur)
    }

    if (options.skinTypes.isAvailable(SkinType.Scales)) {
        showColorRarityMap("Scale Colors", options.scalesColors)
    }
}

// edit

fun HtmlBlockTag.editSkin(state: State, appearance: RaceAppearance) {
    h3 { +"Skin" }

    editSkinInternal(state, appearance.skin, SKIN)
}

fun HtmlBlockTag.editSkinInternal(state: State, options: SkinOptions, param: String) {
    selectRarityMap("Type", combine(param, TYPE), options.skinTypes)

    if (options.skinTypes.isAvailable(SkinType.Exotic)) {
        selectColorRarityMap(
            "Exotic Skin Colors",
            combine(param, EXOTIC, COLOR),
            options.exoticColors,
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Fur) && options.fur != null) {
        selectMaterial(
            state,
            state.sortMaterials(MaterialCategoryType.Fur),
            options.fur,
            combine(param, FUR),
            "Fur",
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Material)) {
        selectRarityMap(
            "Materials",
            combine(param, MATERIAL),
            state.getMaterialStorage(),
            options.materials,
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Normal)) {
        selectSkinColorRarityMap(
            CHARACTER_CONFIG,
            "Normal Skin Colors",
            combine(param, NORMAL, COLOR),
            options.normalColors,
        )
    }

    if (options.skinTypes.isAvailable(SkinType.Scales)) {
        selectColorRarityMap("Scale Colors", combine(param, SCALE, COLOR), options.scalesColors)
    }
}

// parse

fun parseSkinOptions(
    state: State,
    parameters: Parameters,
    param: String,
): SkinOptions {
    val skinTypes = parseOneOf(parameters, combine(param, TYPE), SkinType::valueOf, setOf(SkinType.Normal))
    val fur = if (skinTypes.contains(SkinType.Fur)) {
        parseMaterialId(state, parameters, combine(param, FUR), MaterialCategoryType.Fur)
    } else {
        null
    }

    return SkinOptions(
        skinTypes,
        parseColorOneOf(parameters, combine(param, EXOTIC, COLOR), setOf(DEFAULT_EXOTIC_COLOR)),

        fur,
        parseOneOf(parameters, combine(param, MATERIAL), ::parseMaterialId, setOf(MaterialId(0))),
        parseOneOf(parameters, combine(param, NORMAL, COLOR), SkinColor::valueOf, SkinColor.entries),
        parseColorOneOf(parameters, combine(param, SCALE, COLOR), setOf(DEFAULT_SCALE_COLOR)),
    )
}
