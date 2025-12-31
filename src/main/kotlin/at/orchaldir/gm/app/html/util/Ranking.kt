package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.RankingEntry
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun <ID : Id<ID>, ELEMENT: Element<ID>> HtmlBlockTag.showRankingOfElements(
    call: ApplicationCall,
    state: State,
    total: Int,
    abstractElements: List<ELEMENT>,
    entries: List<RankingEntry<ID>>
) {

    if (abstractElements.isEmpty() && entries.isEmpty()) {
        return
    }

    val firstId = abstractElements.firstOrNull()?.id() ?: entries.first().id

    h3 { +firstId.plural() }

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

    fieldElements(call, state, "Unknown Number In", abstractElements)
}
