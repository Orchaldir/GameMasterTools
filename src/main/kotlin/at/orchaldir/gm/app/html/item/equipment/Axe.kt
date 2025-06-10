package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.AXE
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.item.equipment.TwoHandedAxe
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOneHandedAxe(
    call: ApplicationCall,
    state: State,
    axe: OneHandedAxe,
) {
    showAxeHead(call, state, axe.head)
    showShaft(call, state, axe.shaft)
}

fun HtmlBlockTag.showTwoHandedAxe(
    call: ApplicationCall,
    state: State,
    axe: TwoHandedAxe,
) {
    showAxeHead(call, state, axe.head)
    showShaft(call, state, axe.shaft)
}

// edit

fun FORM.editOneHandedAxe(
    state: State,
    axe: OneHandedAxe,
) {
    editAxeHead(state, axe.head, AXE)
    editShaft(state, axe.shaft)
}

fun FORM.editTwoHandedAxe(
    state: State,
    axe: TwoHandedAxe,
) {
    editAxeHead(state, axe.head, AXE)
    editShaft(state, axe.shaft)
}

// parse

fun parseOneHandedAxe(parameters: Parameters) = OneHandedAxe(
    parseAxeHead(parameters, AXE),
    parseShaft(parameters),
)

fun parseTwoHandedAxe(parameters: Parameters) = TwoHandedAxe(
    parseAxeHead(parameters, AXE),
    parseShaft(parameters),
)