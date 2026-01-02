package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipmentMapUpdate(
    call: ApplicationCall,
    state: State,
    base: EquipmentIdMap,
    update: EquipmentMapUpdate,
) {
    fieldList("Removed", update.removed) { set ->
        val equipment = base.getEquipment(set) ?: error("No equipment for removed $set!")

        link(call, state, equipment.first)
    }

    showEquipmentMap(call, state, "Added", update.added)
}

// edit

fun HtmlBlockTag.editEquipmentMapUpdate(
    call: ApplicationCall,
    state: State,
    base: EquipmentIdMap,
    update: EquipmentMapUpdate,
    param: String,
) {
    val updated = update.applyTo(base)

    editEquipmentMap(
        state,
        updated,
        combine(param, EQUIPMENT),
    )
}

// parse

fun parseEquipmentMapUpdate(
    parameters: Parameters,
    param: String,
    base: EquipmentIdMap,
): EquipmentMapUpdate {
    val paramStart = combine(param, EQUIPMENT)
    if (parameters.filter { parameter, _ -> parameter.startsWith(paramStart) }.isEmpty()) {
        return EquipmentMapUpdate()
    }

    val updated = parseEquipmentMap(parameters, paramStart)

    return EquipmentMapUpdate.calculateUpdate(base, updated)
}
