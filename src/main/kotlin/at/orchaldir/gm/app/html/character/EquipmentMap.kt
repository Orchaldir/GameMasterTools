package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
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
    param: String = "",
) {
    EquipmentDataType.entries.forEach { selectEquipment(state, equipmentMap, it, param) }
}

private fun FORM.selectEquipment(
    state: State,
    equipmentMap: EquipmentMap<EquipmentId>,
    type: EquipmentDataType,
    param: String,
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
                    param + bodySlots.joinToString("_"),
                    options,
                    false,
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
    param: String = "",
): EquipmentMap<EquipmentId> {
    val map = mutableMapOf<EquipmentId, MutableSet<Set<BodySlot>>>()

    parameters.forEach { parameter, ids ->
        if (parameter.startsWith(param)) {
            val slotsString = parameter.removePrefix(param)
            tryParse(map, slotsString, ids)
        }
    }

    return EquipmentMap(map)
}

private fun tryParse(
    map: MutableMap<EquipmentId, MutableSet<Set<BodySlot>>>,
    slotsString: String,
    ids: List<String>,
) {
    val filteredIds = ids.filter { it.isNotEmpty() }
    require(filteredIds.size <= 1) { "Slots $slotsString has too many items!" }
    val id = EquipmentId(filteredIds.firstOrNull()?.toInt() ?: return)

    val slots = slotsString.split("_")
        .map { BodySlot.valueOf(it) }
        .toSet()

    map.computeIfAbsent(id) { mutableSetOf() }
        .add(slots)
}