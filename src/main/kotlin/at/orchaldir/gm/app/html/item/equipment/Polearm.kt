package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.TOP
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Polearm
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPolearm(
    call: ApplicationCall,
    state: State,
    polearm: Polearm,
) {
    showPolearmHead(call, state, polearm.head, "Head")
    showShaft(call, state, polearm.shaft)
}

// edit

fun FORM.editPolearm(
    state: State,
    polearm: Polearm,
) {
    editPolearmHead(state, polearm.head, TOP, "Head")
    editShaft(state, polearm.shaft)
}

// parse

fun parsePolearm(parameters: Parameters) = Polearm(
    parsePolearmHead(parameters, TOP),
    parseShaft(parameters),
)