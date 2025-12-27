package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.INCOME
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.displayPrice
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.selectPrice
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayIncome(
    call: ApplicationCall,
    state: State,
    income: Income,
) {
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> link(call, state, income.standard)
        is Salary -> displayPrice(call, state, income.yearlySalary)
    }
}

fun HtmlBlockTag.showIncome(
    call: ApplicationCall,
    state: State,
    income: Income,
) {
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> fieldLink(call, state, income.standard)
        is Salary -> fieldPrice(call, state, "Average Yearly Salary", income.yearlySalary)
    }
}

// edit

fun HtmlBlockTag.editIncome(
    state: State,
    income: Income,
    param: String = INCOME,
) {
    showDetails("Income", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            state.data.economy.defaultIncomeType.getValidTypes(),
            income.getType(),
        )
        when (income) {
            UndefinedIncome -> doNothing()
            is AffordableStandardOfLiving -> selectElement(
                state,
                combine(param, STANDARD),
                state.data.economy.standardsOfLiving,
                income.standard,
            )

            is Salary -> selectPrice(
                state,
                "Average Yearly Salary",
                income.yearlySalary,
                combine(param, PRICE),
                1,
                100000,
            )
        }
    }
}

// parse

fun parseIncome(
    state: State,
    parameters: Parameters,
    param: String = INCOME,
) =
    when (parse(parameters, combine(param, TYPE), IncomeType.Undefined)) {
        IncomeType.Undefined -> UndefinedIncome
        IncomeType.StandardOfLiving -> AffordableStandardOfLiving(
            parseStandardOfLivingId(parameters, combine(param, STANDARD)),
        )

        IncomeType.Salary -> Salary(
            parsePrice(state, parameters, combine(param, PRICE)),
        )

    }
