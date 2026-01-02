package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.ACCESSORY_RARITY
import at.orchaldir.gm.app.CLOTHING_SETS
import at.orchaldir.gm.app.html.item.equipment.parseEquipmentId
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseOneOf
import at.orchaldir.gm.app.html.parseOneOrNone
import at.orchaldir.gm.app.html.parseSomeOf
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.ClothingFashion
import at.orchaldir.gm.core.model.culture.fashion.ClothingSet
import at.orchaldir.gm.core.model.item.equipment.ACCESSORIES
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.MAIN_EQUIPMENT
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentId
import at.orchaldir.gm.core.selector.item.equipment.isAvailable
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showClothingFashion(
    call: ApplicationCall,
    state: State,
    fashion: ClothingFashion,
) {
    h2 { +"Clothing" }

    showRarityMap("Clothing Sets", fashion.clothingSets)
    showRarityMap("Accessories", fashion.accessories, ACCESSORIES)
    EquipmentDataType.entries.forEach { type ->
        if (MAIN_EQUIPMENT.contains(type) || fashion.accessories.isAvailable(type)) {
            val options = fashion.getOptions(type)

            if (options.isNotEmpty()) {
                showRarityMap(type.name, options) { id ->
                    link(call, state, id)
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editClothingFashion(
    state: State,
    fashion: ClothingFashion,
) {
    h2 { +"Clothing" }

    val availableSets = ClothingSet
        .entries
        .filter { state.isAvailable(it) }
        .toSet()
    val availableAccessories = ACCESSORIES
        .filter { state.isAvailable(it) }
        .toSet()

    selectRarityMap("Clothing Sets", CLOTHING_SETS, fashion.clothingSets, availableSets)
    selectRarityMap("Accessories", ACCESSORY_RARITY, fashion.accessories, availableAccessories)

    EquipmentDataType.entries.forEach { type ->
        if (MAIN_EQUIPMENT.contains(type) || fashion.accessories.isAvailable(type)) {
            selectEquipmentType(state, fashion, type)
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
        selectRarityMap(
            type.name,
            type.name,
            state.getEquipmentStorage(),
            items,
            options,
        ) { it.name.text }
    }
}

// parse

fun parseClothingFashion(parameters: Parameters): ClothingFashion {
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

