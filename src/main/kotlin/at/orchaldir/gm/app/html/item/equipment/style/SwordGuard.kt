package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSwordGuard(
    call: ApplicationCall,
    state: State,
    guard: SwordGuard,
    label: String = "Guard",
) {
    showDetails(label) {
        field("Type", guard.getType())

        when (guard) {
            NoSwordGuard -> doNothing()
            is SimpleSwordGuard -> showSimpleSwordGuard(call, state, guard)
        }
    }
}

private fun DETAILS.showSimpleSwordGuard(
    call: ApplicationCall,
    state: State,
    guard: SimpleSwordGuard,
) {
    fieldFactor("Height relative to Character", guard.height)
    fieldFactor("Width relative to Grip", guard.width)
    showFillLookupItemPart(call, state, guard.part)
}

// edit

fun HtmlBlockTag.editSwordGuard(
    state: State,
    guard: SwordGuard,
    param: String = GUARD,
    label: String = "Guard",
) {
    showDetails(label, true) {
        selectValue("Type", param, SwordGuardType.entries, guard.getType())

        when (guard) {
            NoSwordGuard -> doNothing()
            is SimpleSwordGuard -> editSimpleSwordGuard(state, guard, param)
        }
    }
}

private fun DETAILS.editSimpleSwordGuard(
    state: State,
    guard: SimpleSwordGuard,
    param: String,
) {
    selectFactor(
        "Height relative to Character",
        combine(param, LENGTH),
        guard.height,
        MIN_GUARD_HEIGHT,
        MAX_GUARD_HEIGHT,
    )
    selectFactor(
        "Width relative to Grip",
        combine(param, WIDTH),
        guard.width,
        MIN_GUARD_WIDTH,
        MAX_GUARD_WIDTH,
    )
    editFillLookupItemPart(state, guard.part, param)
}

// parse

fun parseSwordGuard(
    parameters: Parameters,
    param: String = GUARD,
) = when (parse(parameters, param, SwordGuardType.None)) {
    SwordGuardType.None -> NoSwordGuard
    SwordGuardType.Simple -> parseSimpleSwordGuard(parameters, param)
}

private fun parseSimpleSwordGuard(parameters: Parameters, param: String) = SimpleSwordGuard(
    parseGuardLength(parameters, param),
    parseGuardWidth(parameters, param),
    parseFillLookupItemPart(parameters, param),
)

private fun parseGuardLength(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LENGTH), DEFAULT_GUARD_HEIGHT)

private fun parseGuardWidth(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, WIDTH), DEFAULT_GUARD_WIDTH)