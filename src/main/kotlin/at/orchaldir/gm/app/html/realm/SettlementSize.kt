package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.POPULATION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementSize
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.name.Name
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSettlementSize(
    call: ApplicationCall,
    state: State,
    size: SettlementSize,
) {
    field("Max Yearly Income", size.maxPopulation)
}

// edit

fun HtmlBlockTag.editSettlementSize(
    state: State,
    size: SettlementSize,
    param: String,
    minMaxPopulation: Int,
) {
    selectName(size.name, combine(param, NAME))
    selectInt(
        "Max Population",
        size.maxPopulation,
        minMaxPopulation,
        10000000,
        10,
        combine(param, POPULATION),
    )
}

// parse

fun parseSettlementSizeId(parameters: Parameters, param: String) = SettlementSizeId(parseInt(parameters, param))

fun parseSettlementSize(
    id: SettlementSizeId,
    parameters: Parameters,
    param: String,
) = SettlementSize(
    id,
    parseOptionalName(parameters, combine(param, NAME)) ?: Name.init(id),
    parseInt(parameters, combine(param, POPULATION)),
)
