package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.ACCESSORY_RARITY
import at.orchaldir.gm.app.CLOTHING_SETS
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.item.equipment.parseEquipmentId
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.parse.parseOneOrNone
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.culture.fashion.ClothingFashion
import at.orchaldir.gm.core.model.item.equipment.ACCESSORIES
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.MAIN_EQUIPMENT
import at.orchaldir.gm.core.selector.item.getEquipmentId
import at.orchaldir.gm.core.selector.item.isAvailable
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showClothingStyle(
    call: ApplicationCall,
    state: State,
    style: ClothingFashion,
) {
    h2 { +"Clothing" }

    showRarityMap("Clothing Sets", style.clothingSets)
    showRarityMap("Accessories", style.accessories, ACCESSORIES)
    EquipmentDataType.entries.forEach { type ->
        if (MAIN_EQUIPMENT.contains(type) || style.accessories.isAvailable(type)) {
            val options = style.getOptions(type)

            if (options.isNotEmpty()) {
                showRarityMap(type.name, options) { id ->
                    link(call, state, id)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editClothingStyle(
    state: State,
    style: ClothingFashion,
) {
    h2 { +"Clothing" }

    val availableSets = ClothingSet
        .entries
        .filter { state.isAvailable(it) }
        .toSet()
    val availableAccessories = ACCESSORIES
        .filter { state.isAvailable(it) }
        .toSet()

    selectRarityMap("Clothing Sets", CLOTHING_SETS, style.clothingSets, true, availableSets)
    selectRarityMap("Accessories", ACCESSORY_RARITY, style.accessories, true, availableAccessories)

    EquipmentDataType.entries.forEach { type ->
        if (MAIN_EQUIPMENT.contains(type) || style.accessories.isAvailable(type)) {
            selectEquipmentType(state, style, type)
        }
    }
}

private fun HtmlBlockTag.selectEquipmentType(
    state: State,
    style: ClothingFashion,
    type: EquipmentDataType,
) {
    val items = state.getEquipmentId(type)

    if (items.isNotEmpty()) {
        val options = style.getOptions(type)
        selectRarityMap(type.name, type.name, state.getEquipmentStorage(), items, options) { it.name }
    }
}

// parse

fun parseClothingStyle(parameters: Parameters): ClothingFashion {
    val accessories = parseSomeOf(parameters, ACCESSORY_RARITY, EquipmentDataType::valueOf)

    return ClothingFashion(
        parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
        accessories,
        EquipmentDataType.entries
            .filter { type -> MAIN_EQUIPMENT.contains(type) || accessories.isAvailable(type) }
            .associateWith { parseEquipmentMap(parameters, it) },
    )
}

private fun parseEquipmentMap(parameters: Parameters, type: EquipmentDataType) =
    parseOneOrNone(parameters, type.name, ::parseEquipmentId)

