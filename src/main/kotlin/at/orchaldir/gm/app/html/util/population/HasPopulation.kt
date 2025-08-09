package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.selector.util.getElementsWithPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showPopulation(
    call: ApplicationCall,
    state: State,
    race: RaceId,
) {
    h2 { +"Population" }

    showDestroyed(call, state, race, state.getDistrictStorage())
    showDestroyed(call, state, race, state.getRealmStorage())
    showDestroyed(call, state, race, state.getTownStorage())
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showDestroyed(
    call: ApplicationCall,
    state: State,
    race: RaceId,
    storage: Storage<ID, ELEMENT>,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    val elements = getElementsWithPopulation(storage, race)
    val total = elements.sumOf { it.second }

    if (elements.isNotEmpty()) {
        table {
            tr {
                th { +elements.first().first.plural() }
                th { +"Percentage" }
                th { +"Number" }
            }
            elements
                .sortedByDescending { it.second }
                .forEach { (element, population) ->
                    val percentage = Factor.fromNumber(population / total.toFloat())

                    tr {
                        tdLink(call, state, element)
                        showPercentageAndNumber(total, percentage)
                    }
                }
        }
    }
}
