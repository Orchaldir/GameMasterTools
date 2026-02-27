package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.MAX
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementSize
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.selector.realm.getSettlements
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showSettlementSize(
    call: ApplicationCall,
    state: State,
    size: SettlementSize,
) {
    field("Max Population", size.maxPopulation)

    showUsages(call, state, size.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    size: SettlementSizeId,
) {
    val settlements = state.getSettlements(size)

    if (settlements.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, settlements)
}

// edit

fun HtmlBlockTag.editSettlementSize(
    call: ApplicationCall,
    state: State,
    size: SettlementSize,
) {
    selectName(size.name, NAME)
    selectInt(
        "Max Population",
        size.maxPopulation,
        0,
        10000000,
        1,
        combine(MAX, POPULATION),
    )
}

// parse

fun parseSettlementSizeId(parameters: Parameters, param: String) = SettlementSizeId(parseInt(parameters, param))

fun parseSettlementSize(
    state: State,
    parameters: Parameters,
    id: SettlementSizeId,
) = SettlementSize(
    id,
    parseOptionalName(parameters, NAME) ?: Name.init(id),
    parseInt(parameters, combine(MAX, POPULATION)),
)
