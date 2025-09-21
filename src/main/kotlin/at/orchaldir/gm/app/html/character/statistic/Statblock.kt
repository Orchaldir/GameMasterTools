package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statblock
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    showDetails("Stateblock", true) {

    }
}

// edit

fun FORM.editStatblock(
    state: State,
    statblock: Statblock,
) {
    showDetails("Stateblock", true) {

    }
}

// parse

fun parseStatblock(
    parameters: Parameters,
) = Statblock()
