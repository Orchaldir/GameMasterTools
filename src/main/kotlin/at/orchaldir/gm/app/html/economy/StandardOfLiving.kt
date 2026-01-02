package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.selectPrice
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.realm.getPopulationsWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStandardOfLiving(
    call: ApplicationCall,
    state: State,
    standard: StandardOfLiving,
) {
    fieldPrice(call, state, "Max Yearly Income", standard.maxYearlyIncome)
    fieldElements(call, state, getPopulationsWith(state.getDistrictStorage(), standard.id))
    fieldElements(call, state, state.getJobs(standard.id))
    fieldElements(call, state, getPopulationsWith(state.getRealmStorage(), standard.id))
    fieldElements(call, state, getPopulationsWith(state.getTownStorage(), standard.id))
}

// edit

fun HtmlBlockTag.editStandardOfLiving(
    state: State,
    standard: StandardOfLiving,
    param: String,
    minIncome: Int,
) {
    selectName(standard.name, combine(param, NAME))
    selectPrice(
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
    state: State,
    id: StandardOfLivingId,
    parameters: Parameters,
    param: String,
) = StandardOfLiving(
    id,
    parseName(parameters, combine(param, NAME), "Standard ${id.value}"),
    parsePrice(state, parameters, combine(param, PRICE)),
)
