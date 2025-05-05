package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.economy.money.editPrice
import at.orchaldir.gm.app.html.model.economy.money.parsePrice
import at.orchaldir.gm.app.html.model.economy.money.showPrice
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.getJobs
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStandardOfLiving(
    call: ApplicationCall,
    state: State,
    standard: StandardOfLiving,
) {
    showPrice(state, "Max Yearly Income", standard.maxYearlyIncome)
    fieldList(call, state, state.getJobs(standard.id))
}

// edit

fun HtmlBlockTag.editStandardOfLiving(
    state: State,
    standard: StandardOfLiving,
    param: String,
    minIncome: Int,
) {
    selectName(standard.name, combine(param, NAME))
    editPrice(
        state,
        "Max Yearly Income",
        standard.maxYearlyIncome,
        combine(param, PRICE),
        minIncome,
        10000000,
    )
}

// parse

fun parseStandardOfLivingId(parameters: Parameters, param: String) = StandardOfLivingId(parseInt(parameters, param))

fun parseStandardOfLiving(
    id: StandardOfLivingId,
    parameters: Parameters,
    param: String,
) = StandardOfLiving(
    id,
    parseName(parameters, combine(param, NAME), "Standard ${id.value}"),
    parsePrice(parameters, combine(param, PRICE)),
)
