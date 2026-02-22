package at.orchaldir.gm.app.html.realm.population

import at.orchaldir.gm.app.DENSITY
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.TOTAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.parseSettlementSizeId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.population.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.util.sortSettlementSizes
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayTotalPopulation(
    call: ApplicationCall,
    state: State,
    total: TotalPopulation,
) {
    when (total) {
        is TotalPopulationAsDensity -> +total.density.toString()
        is TotalPopulationAsNumber -> +total.number.toString()
        is TotalPopulationAsSettlementSize -> link(call, state, total.size)
    }
}

fun HtmlBlockTag.fieldTotalPopulation(
    call: ApplicationCall,
    state: State,
    total: TotalPopulation,
) = field("Total Population") {
    displayTotalPopulation(call, state, total)
}

// edit

fun HtmlBlockTag.editTotalPopulation(
    state: State,
    total: TotalPopulation,
    param: String,
) {
    val totalParam = combine(param, TOTAL)

    showDetails("Total Population", true) {
        selectValue("Type", totalParam, TotalPopulationType.entries, total.getType())

        when (total) {
            is TotalPopulationAsDensity -> selectValue(
                "Density",
                combine(totalParam, DENSITY),
                Size.entries,
                total.density,
            )
            is TotalPopulationAsNumber -> selectInt(
                "Number",
                total.number,
                0,
                Int.MAX_VALUE,
                1,
                combine(totalParam, NUMBER),
            )
            is TotalPopulationAsSettlementSize -> selectUnsortedElement(
                state,
                "Settlement Size",
                combine(totalParam, SIZE),
                state.sortSettlementSizes(),
                total.size,
            )
        }
    }
}

// parse

fun parseTotalPopulation(
    parameters: Parameters,
    param: String,
): TotalPopulation {
    val totalParam = combine(param, TOTAL)

    return when (parse(parameters, totalParam, TotalPopulationType.Density)) {
        TotalPopulationType.Density -> TotalPopulationAsDensity(
            parse(parameters, combine(totalParam, DENSITY), Size.Medium),
        )

        TotalPopulationType.Number -> TotalPopulationAsNumber(
            parseInt(parameters, combine(totalParam, NUMBER), 0),
        )
        TotalPopulationType.SettlementSize -> TotalPopulationAsSettlementSize(
            parseSettlementSizeId(parameters, combine(totalParam, SIZE)),
        )
    }
}
