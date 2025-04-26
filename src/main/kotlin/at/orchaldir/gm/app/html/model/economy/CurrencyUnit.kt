package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCurrencyUnit(
    call: ApplicationCall,
    state: State,
    unit: CurrencyUnit,
) {
    fieldLink("Currency", call, state, unit.currency)
    field("Value", unit.value)
}

// edit

fun FORM.editCurrencyUnit(
    state: State,
    unit: CurrencyUnit,
) {
    selectName(unit.name)
    selectElement(
        state,
        "Currency",
        CURRENCY,
        state.getCurrencyStorage().getAll(),
        unit.currency,
    )
    selectInt("Value", unit.value, 1, 10000, 1, NUMBER)
}

// parse

fun parseCurrencyUnitId(parameters: Parameters, param: String) =
    parseOptionalCurrencyUnitId(parameters, param) ?: CurrencyUnitId(0)

fun parseOptionalCurrencyUnitId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { CurrencyUnitId(it) }

fun parseCurrencyUnit(parameters: Parameters, state: State, id: CurrencyUnitId): CurrencyUnit = CurrencyUnit(
    id,
    parseName(parameters),
    parseCurrencyId(parameters, CURRENCY),
    parseInt(parameters, NUMBER, 1),
)
