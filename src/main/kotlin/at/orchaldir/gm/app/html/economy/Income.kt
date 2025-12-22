package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.economy.money.editPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showIncome(
    call: ApplicationCall,
    state: State,
    income: Income,
) {
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> fieldLink(call, state, income.standard)
        is Salary -> fieldPrice(state, "Average Yearly Salary", income.yearlySalary)
    }
}

// edit

fun HtmlBlockTag.editIncome(
    state: State,
    income: Income,
) {
    showDetails("Income", true) {
        selectValue(
            "Type",
            combine(PRICE, TYPE),
            state.data.economy.defaultIncomeType.getValidTypes(),
            income.getType(),
        )
        when (income) {
            UndefinedIncome -> doNothing()
            is AffordableStandardOfLiving -> selectElement(
                state,
                STANDARD,
                state.data.economy.standardsOfLiving,
                income.standard,
            )

            is Salary -> editPrice(state, "Average Yearly Salary", income.yearlySalary, PRICE, 1, 100000)
        }
    }
}

// parse

fun parseIncome(parameters: Parameters) =
    when (parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined)) {
        IncomeType.Undefined -> UndefinedIncome
        IncomeType.StandardOfLiving -> AffordableStandardOfLiving(
            parseStandardOfLivingId(parameters, STANDARD),
        )

        IncomeType.Salary -> Salary(
            parsePrice(parameters, PRICE)
        )

    }
