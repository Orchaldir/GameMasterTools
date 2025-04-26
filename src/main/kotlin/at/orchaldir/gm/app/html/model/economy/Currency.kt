package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.economy.getCurrencyUnits
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showCurrency(
    call: ApplicationCall,
    state: State,
    currency: Currency,
) {
    optionalField(call, state, "Start", currency.startDate)
    optionalField(call, state, "End", currency.endDate)

    val units = state.sortCurrencyUnits(
        state.getCurrencyUnits(currency.id),
        SortCurrencyUnit.Value,
    )
    table {
        tr {
            th { +"Value" }
            th { +"Unit" }
        }
        units.forEach { unit ->
            tr {
                tdSkipZero(unit.value)
                td { link(call, state, unit) }
            }
        }
    }
}

// edit

fun FORM.editCurrency(
    state: State,
    currency: Currency,
) {
    selectName(currency.name)
    selectOptionalDate(state, "Start", currency.startDate, combine(START, DATE))
    selectOptionalDate(state, "End", currency.endDate, combine(END, DATE))
}

// parse

fun parseCurrencyId(parameters: Parameters, param: String) = parseOptionalCurrencyId(parameters, param) ?: CurrencyId(0)
fun parseOptionalCurrencyId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { CurrencyId(it) }

fun parseCurrency(parameters: Parameters, state: State, id: CurrencyId): Currency = Currency(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, combine(START, DATE)),
    parseOptionalDate(parameters, state, combine(END, DATE)),
)
