package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.ATTRIBUTE
import at.orchaldir.gm.app.RANK
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseOptionalInt
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.tdEnum
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statblock
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.selector.character.getAttributes
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val attributeValues = attributes.mapNotNull { attribute ->
        statblock.resolve(state, attribute)?.let { Pair(attribute, it) }
    }

    showDetails("Stateblock", true) {
        fieldList("Attributes", attributeValues) { (attribute , value) ->
            link(call, state, attribute)
            +": $value"
        }
    }
}

// edit

fun FORM.editStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())

    showDetails("Stateblock", true) {
        table {
            tr {
                th { +"Attribute" }
                th { +"Offset" }
                th { +"Result" }
            }
            attributes.forEach { attribute ->
                val offset = statblock.statistics[attribute.id] ?: 0
                val value = statblock.resolve(state, attribute) ?: return@forEach

                tr {
                    tdLink(call, state, attribute)
                    td {
                        selectInt(
                            offset,
                            -10,
                            +10,
                            1,
                            combine(ATTRIBUTE, attribute.id.value),
                        )
                    }
                    tdSkipZero(value)
                }
            }
        }
    }
}

// parse

fun parseStatblock(
    state: State,
    parameters: Parameters,
): Statblock {
    val values = mutableMapOf<StatisticId,Int>()

    state.getAttributes().forEach { attribute ->
        parseSimpleOptionalInt(parameters, combine(ATTRIBUTE, attribute.id.value))?.let {
            values[attribute.id] = it
        }
    }

    return Statblock(values)
}
