package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.color.parseColorSchemeId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.item.getEquipmentOf
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipmentMap(
    call: ApplicationCall,
    state: State,
    label: String,
    equipmentMap: EquipmentIdMap,
) {
    fieldList(label, equipmentMap.getEquipmentWithSlotSets()) { (pair, slotSets) ->
        link(call, state, pair.first)
        if (pair.second != null) {
            +" ("
            link(call, state, pair.second!!)
            +")"
        }

        if (slotSets.size > 1) {
            showList(slotSets) { slots ->
                +slots.joinToString()
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEquipmentMap(
    state: State,
    equipmentMap: EquipmentIdMap,
    param: String,
) {
    EquipmentDataType.entries.forEach { selectEquipment(state, equipmentMap, it, param) }
}

private fun HtmlBlockTag.selectEquipment(
    state: State,
    equipmentMap: EquipmentIdMap,
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
            val currentPair = equipmentMap.getEquipment(bodySlots)
            val currentId = currentPair?.first
            val optionalEquipment = state.getEquipmentStorage().getOptional(currentId)
            val isFreeOrType = isFree || optionalEquipment?.data?.isType(type) ?: false
            val text = bodySlots.joinToString(" & ")

            if (isFreeOrType) {
                val slotsParam = param + bodySlots.joinToString("_")
                val currentSchema = currentPair?.second

                selectFromOneOrNone(
                    text,
                    slotsParam,
                    options,
                    false,
                ) { id ->
                    val equipment = state.getEquipmentStorage().getOrThrow(id)
                    label = equipment.name.text
                    value = id.value.toString()
                    selected = id == currentId
                }

                if (optionalEquipment != null && currentSchema != null) {
                    selectElement(
                        state,
                        combine(COLOR, slotsParam),
                        state.getColorSchemeStorage().get(optionalEquipment.colorSchemes),
                        currentSchema,
                    )
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
    param: String,
): EquipmentIdMap {
    val map = mutableMapOf<EquipmentIdPair, MutableSet<Set<BodySlot>>>()

    parameters.forEach { parameter, ids ->
        if (parameter.startsWith(param)) {
            val slotsString = parameter.removePrefix(param)
            val scheme = parseColorSchemeId(parameters, combine(COLOR, parameter))
            tryParse(map, slotsString, ids, scheme)
        }
    }

    return EquipmentMap.fromSlotAsValueMap(map)
}

private fun tryParse(
    map: MutableMap<EquipmentIdPair, MutableSet<Set<BodySlot>>>,
    slotsString: String,
    ids: List<String>,
    scheme: ColorSchemeId,
) {
    val filteredIds = ids.filter { it.isNotEmpty() }
    require(filteredIds.size <= 1) { "Slots $slotsString has too many items!" }
    val id = EquipmentId(filteredIds.firstOrNull()?.toInt() ?: return)
    val pair = Pair(id, scheme)

    val slots = slotsString.split("_")
        .map { BodySlot.valueOf(it) }
        .toSet()

    map.computeIfAbsent(pair) { mutableSetOf() }
        .add(slots)
}