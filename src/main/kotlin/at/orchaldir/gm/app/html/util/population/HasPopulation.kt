package at.orchaldir.gm.app.html.util.population

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.selector.util.getAbstractPopulations
import at.orchaldir.gm.core.selector.util.getPopulationEntries
import at.orchaldir.gm.core.selector.util.calculatePopulationIndex
import at.orchaldir.gm.core.selector.util.calculateTotalPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showPopulation(
    call: ApplicationCall,
    state: State,
    race: RaceId,
) {
    h2 { +"Population" }

    optionalField("Total", state.calculateTotalPopulation(race))
    optionalField("Index", state.calculatePopulationIndex(race))

    showPopulation(call, state, race, state.getDistrictStorage())
    showPopulation(call, state, race, state.getRealmStorage())
    showPopulation(call, state, race, state.getTownStorage())
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showPopulation(
    call: ApplicationCall,
    state: State,
    race: RaceId,
    storage: Storage<ID, ELEMENT>,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPopulation {
    val entries = getPopulationEntries(storage, race)
    val total = entries.sumOf { it.number }

    if (entries.isNotEmpty()) {
        val id = entries.first().id

        table {
            tr {
                th { +id.plural() }
                thMultiLines(listOf("Percentage", "of", "Total"))
                thMultiLines(listOf("Percentage", "of", id.type()))
                th { +"Number" }
            }
            entries
                .sortedByDescending { it.number }
                .forEach {
                    val percentageOfTotal = Factor.fromNumber(it.number / total.toFloat())

                    tr {
                        tdLink(call, state, it.id)
                        tdPercentage(percentageOfTotal)
                        tdPercentage(it.percentage)
                        tdSkipZero(it.number)
                    }
                }
        }
    }

    fieldElements(call, state, "Abstract Population", getAbstractPopulations(storage, race))
}
