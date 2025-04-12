package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.ACCESSORY_RARITY
import at.orchaldir.gm.app.CLOTHING_SETS
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.equipment.parseEquipmentId
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.parse.parseOneOrNone
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.ClothingStyle
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.item.equipment.ACCESSORIES
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.selector.item.getEquipmentId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFashion(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    field("Name", fashion.name)
    showAppearanceStyle(fashion.appearance)
    showClothingStyle(call, state, fashion.clothing)
}

private fun HtmlBlockTag.showClothingStyle(
    call: ApplicationCall,
    state: State,
    style: ClothingStyle,
) {
    showRarityMap("Clothing Sets", style.clothingSets)
    showRarityMap("Accessories", style.accessories, ACCESSORIES)
    EquipmentDataType.entries.forEach {
        val options = style.getOptions(it)

        if (options.isNotEmpty()) {
            showRarityMap(it.name, options) { id ->
                link(call, state, id)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editFashion(
    fashion: Fashion,
    state: State,
) {
    selectName(fashion.name)
    editAppearanceOptions(fashion.appearance)
    editClothingStyle(state, fashion.clothing)
}

private fun HtmlBlockTag.editClothingStyle(
    state: State,
    style: ClothingStyle,
) {
    selectRarityMap("Clothing Sets", CLOTHING_SETS, style.clothingSets)
    selectRarityMap("Accessories", ACCESSORY_RARITY, style.accessories, false, ACCESSORIES)
    EquipmentDataType.entries.forEach {
        selectEquipmentType(state, style, it)
    }
}

private fun HtmlBlockTag.selectEquipmentType(
    state: State,
    style: ClothingStyle,
    type: EquipmentDataType,
) {
    val items = state.getEquipmentId(type)

    if (items.isNotEmpty()) {
        val options = style.getOptions(type)
        selectRarityMap(type.name, type.name, state.getEquipmentStorage(), items, options) { it.name }
    }
}

// parse

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parseInt(parameters, param))

fun parseFashion(id: FashionId, parameters: Parameters) = Fashion(
    id,
    parameters.getOrFail(NAME),
    parseAppearanceStyle(parameters),
    parseClothingStyle(parameters),
)

fun parseClothingStyle(parameters: Parameters) = ClothingStyle(
    parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
    parseSomeOf(parameters, ACCESSORY_RARITY, EquipmentDataType::valueOf),
    EquipmentDataType.entries
        .associateWith { parseEquipmentMap(parameters, it) },
)

private fun parseEquipmentMap(parameters: Parameters, type: EquipmentDataType) =
    parseOneOrNone(parameters, type.name, ::parseEquipmentId)

