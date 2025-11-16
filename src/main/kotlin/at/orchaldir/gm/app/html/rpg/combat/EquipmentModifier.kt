package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.selector.item.getEquipment
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEquipmentModifier(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifier,
) {
    showUsages(call, state, modifier.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifierId,
) {
    val equipment = state.getEquipment(modifier)

    if (equipment.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, equipment)
}

// edit

fun HtmlBlockTag.editEquipmentModifier(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifier,
) {
    selectName(modifier.name)
}

// parse

fun parseEquipmentModifierId(parameters: Parameters, param: String) =
    EquipmentModifierId(parseInt(parameters, param))

fun parseEquipmentModifierId(value: String) = EquipmentModifierId(value.toInt())

fun parseEquipmentModifier(
    state: State,
    parameters: Parameters,
    id: EquipmentModifierId,
) = EquipmentModifier(
    id,
    parseName(parameters),
)
