package at.orchaldir.gm.app.html.model.character

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.selector.item.getEquipmentOf
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipmentMap(
    call: ApplicationCall,
    state: State,
    label: String,
    equipmentMap: EquipmentMap<EquipmentId>,
) {
    showMap(label, equipmentMap.getEquipmentWithSlotSets()) { item, slotSets ->
        link(call, state, item)

        if (slotSets.size > 1) {
            showList(slotSets) { slots ->
                +slots.joinToString()
            }
        }
    }
}

// edit

fun FORM.editEquipmentMap(
    state: State,
    equipmentMap: EquipmentMap<EquipmentId>,
) {
    EquipmentDataType.entries.forEach { selectEquipment(state, equipmentMap, it) }
}

private fun FORM.selectEquipment(
    state: State,
    equipmentMap: EquipmentMap<EquipmentId>,
    type: EquipmentDataType,
) {
    // ignore fashion for testing
    val options = OneOrNone(state.getEquipmentOf(type).map { it.id })

    if (options.isEmpty()) {
        return
    }

    showDetails(type.name, true) {
        type.slots().getAllBodySlotCombinations().forEach { bodySlots ->
            val isFree = equipmentMap.isFree(bodySlots)
            val currentId = equipmentMap.getEquipment(bodySlots)
            val isFreeOrType = isFree || state.getEquipmentStorage().getOptional(currentId)?.data?.isType(type) ?: false
            val text = bodySlots.joinToString(" & ")

            if (isFreeOrType) {
                selectFromOneOrNone(
                    text,
                    bodySlots.joinToString("_"),
                    options,
                    false,
                    true,
                ) { id ->
                    val equipment = state.getEquipmentStorage().getOrThrow(id)
                    label = equipment.name.text
                    value = id.value.toString()
                    selected = id == currentId
                }
            } else {
                field(text, "Slot(s) are occupied")
            }
        }
    }
}

// parse

fun parseEquipmentMap(
    parameters: Parameters,
): EquipmentMap<EquipmentId> {
    val map = mutableMapOf<EquipmentId, MutableSet<Set<BodySlot>>>()

    parameters.forEach { slotStrings, ids ->
        tryParse(map, slotStrings, ids)
    }

    return EquipmentMap(map)
}

private fun tryParse(
    map: MutableMap<EquipmentId, MutableSet<Set<BodySlot>>>,
    slotStrings: String,
    ids: List<String>,
) {
    val filteredIds = ids.filter { it.isNotEmpty() }
    require(filteredIds.size <= 1) { "Slots $slotStrings has too many items!" }
    val id = EquipmentId(filteredIds.firstOrNull()?.toInt() ?: return)

    val slots = slotStrings.split("_")
        .map { BodySlot.valueOf(it) }
        .toSet()

    map.computeIfAbsent(id) { mutableSetOf() }
        .add(slots)
}