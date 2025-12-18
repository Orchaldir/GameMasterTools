package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.REMOVE
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValues
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdPair
import io.ktor.http.*
import io.ktor.server.application.*
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
    selectValues(
        "Removed",
        combine(param, REMOVE),
        base.getSlotSetWithEquipmentList(),
        update.removed
            .mapNotNull { set ->
                base.getEquipment(set)?.let { Pair(set, it) }
            }.toSet(),
    ) { (set, pair) ->
        link(call, state, pair.first)
        +" $set"
    }
}

// parse

fun parseEquipmentMapUpdate(
    parameters: Parameters,
    param: String,
) = EquipmentMapUpdate()
