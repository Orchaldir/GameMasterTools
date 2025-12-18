package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
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
    state: State,
    base: EquipmentIdMap,
    update: EquipmentMapUpdate,
    param: String,
) {

}

// parse

fun parseEquipmentMapUpdate(
    parameters: Parameters,
    param: String,
) = EquipmentMapUpdate()
