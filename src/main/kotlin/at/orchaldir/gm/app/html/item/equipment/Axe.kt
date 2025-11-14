package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.AXE
import at.orchaldir.gm.app.FIXATION
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.rpg.combat.parseMeleeWeaponStats
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.item.equipment.TwoHandedAxe
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOneHandedAxe(
    call: ApplicationCall,
    state: State,
    axe: OneHandedAxe,
) {
    showAxeHead(call, state, axe.head)
    showHeadFixation(call, state, axe.fixation)
    showShaft(call, state, axe.shaft)
}

fun HtmlBlockTag.showTwoHandedAxe(
    call: ApplicationCall,
    state: State,
    axe: TwoHandedAxe,
) {
    showAxeHead(call, state, axe.head)
    showHeadFixation(call, state, axe.fixation)
    showShaft(call, state, axe.shaft)
}

// edit

fun HtmlBlockTag.editOneHandedAxe(
    state: State,
    axe: OneHandedAxe,
) {
    editAxeHead(state, axe.head, AXE)
    editHeadFixation(state, axe.fixation, FIXATION)
    editShaft(state, axe.shaft)
}

fun HtmlBlockTag.editTwoHandedAxe(
    state: State,
    axe: TwoHandedAxe,
) {
    editAxeHead(state, axe.head, AXE)
    editHeadFixation(state, axe.fixation, FIXATION)
    editShaft(state, axe.shaft)
}

// parse

fun parseOneHandedAxe(
    state: State,
    parameters: Parameters,
) = OneHandedAxe(
    parseAxeHead(parameters, AXE),
    parseHeadFixation(parameters, FIXATION),
    parseShaft(parameters),
    parseMeleeWeaponStats(state, parameters),
)

fun parseTwoHandedAxe(
    state: State,
    parameters: Parameters,
) = TwoHandedAxe(
    parseAxeHead(parameters, AXE),
    parseHeadFixation(parameters, FIXATION),
    parseShaft(parameters),
    parseMeleeWeaponStats(state, parameters),
)