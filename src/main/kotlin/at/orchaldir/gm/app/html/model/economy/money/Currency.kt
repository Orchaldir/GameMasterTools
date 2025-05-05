package at.orchaldir.gm.app.html.model.economy.money

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showCurrency(
    call: ApplicationCall,
    state: State,
    currency: Currency,
) {
    optionalField(call, state, "Start", currency.startDate)
    optionalField(call, state, "End", currency.endDate)
    showDenominations(currency)

    showUnits(state, currency, call)
}

private fun HtmlBlockTag.showDenominations(currency: Currency) {
    showDenomination(currency.denomination)
    fieldList("Subdenominations", currency.subDenominations) { (denomination, threshold) ->
        showDenomination(denomination)
        field("Threshold", threshold.toString())
    }
}

private fun HtmlBlockTag.showUnits(
    state: State,
    currency: Currency,
    call: ApplicationCall,
) {
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
            val denomination = currency.getDenomination(unit.denomination)
            tr {
                td { +denomination.display(unit.number) }
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
    editDenominations(currency)
}

private fun FORM.editDenominations(currency: Currency) {
    editDenomination(currency.denomination, DENOMINATION)
    editList(
        "Subdenominations",
        DENOMINATION,
        currency.subDenominations,
        0,
        3,
        1,
    ) { index, param, (denomination, threshold) ->
        editDenomination(denomination, param)
        selectInt(
            "Threshold",
            threshold,
            1,
            100000,
            1,
            combine(param, NUMBER),
            true
        )
    }
}

// parse

fun parseCurrencyId(parameters: Parameters, param: String) = parseOptionalCurrencyId(parameters, param) ?: CurrencyId(0)
fun parseOptionalCurrencyId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { CurrencyId(it) }

fun parseCurrency(parameters: Parameters, state: State, id: CurrencyId): Currency {
    var lastThreshold = 0
    val subDenominations = parseList(parameters, DENOMINATION, 0) { _, param ->
        lastThreshold = parseInt(parameters, combine(param, NUMBER), lastThreshold + 1)

        Pair(
            parseDenomination(parameters, param),
            lastThreshold
        )
    }

    return Currency(
        id,
        parseName(parameters),
        parseDenomination(parameters, DENOMINATION),
        subDenominations,
        parseOptionalDate(parameters, state, combine(START, DATE)),
        parseOptionalDate(parameters, state, combine(END, DATE)),
    )
}
