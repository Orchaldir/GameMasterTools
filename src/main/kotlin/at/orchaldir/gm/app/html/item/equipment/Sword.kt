package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.OneHandedSword
import at.orchaldir.gm.core.model.item.equipment.TwoHandedSword
import at.orchaldir.gm.core.model.item.equipment.style.DEFAULT_1H_BLADE_LENGTH
import at.orchaldir.gm.core.model.item.equipment.style.DEFAULT_2H_BLADE_LENGTH
import at.orchaldir.gm.core.model.item.equipment.style.MAX_1H_BLADE_LENGTH
import at.orchaldir.gm.core.model.item.equipment.style.MAX_2H_BLADE_LENGTH
import at.orchaldir.gm.core.model.item.equipment.style.MIN_1H_BLADE_LENGTH
import at.orchaldir.gm.core.model.item.equipment.style.MIN_2H_BLADE_LENGTH
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOneHandedSword(
    call: ApplicationCall,
    state: State,
    sword: OneHandedSword,
) {
    showBlade(call, state, sword.blade)
    showSwordHilt(call, state, sword.hilt)
}

fun HtmlBlockTag.showTwoHandedSword(
    call: ApplicationCall,
    state: State,
    sword: TwoHandedSword,
) {
    showBlade(call, state, sword.blade)
    showSwordHilt(call, state, sword.hilt)
}

// edit

fun FORM.editOneHandedSword(
    state: State,
    sword: OneHandedSword,
) {
    editBlade(state, sword.blade, MIN_1H_BLADE_LENGTH, MAX_1H_BLADE_LENGTH)
    editSwordHilt(state, sword.hilt)
}

fun FORM.editTwoHandedSword(
    state: State,
    sword: TwoHandedSword,
) {
    editBlade(state, sword.blade, MIN_2H_BLADE_LENGTH, MAX_2H_BLADE_LENGTH)
    editSwordHilt(state, sword.hilt)
}

// parse

fun parseOneHandedSword(parameters: Parameters) = OneHandedSword(
    parseBlade(parameters, DEFAULT_1H_BLADE_LENGTH),
    parseSwordHilt(parameters),
)

fun parseTwoHandedSword(parameters: Parameters) = TwoHandedSword(
    parseBlade(parameters, DEFAULT_2H_BLADE_LENGTH),
    parseSwordHilt(parameters),
)