package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.BELIEVE
import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PANTHEON
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.religion.parsePantheonId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Atheist
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.BeliefStatusType
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.UndefinedBeliefStatus
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.util.WorshipOfPantheon
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.core.selector.util.sortPantheons
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBeliefStatusHistory(
    call: ApplicationCall,
    state: State,
    history: History<BeliefStatus>,
) = showHistory(call, state, history, "Belief Status", HtmlBlockTag::showBeliefStatus)

fun HtmlBlockTag.showBeliefStatus(
    call: ApplicationCall,
    state: State,
    status: BeliefStatus,
    showUndefined: Boolean = true,
) {
    when (status) {
        UndefinedBeliefStatus -> if (showUndefined) {
            +"Undefined"
        }

        Atheist -> +"Atheist"
        is WorshipOfGod -> link(call, state, status.god)
        is WorshipOfPantheon -> link(call, state, status.pantheon)
    }
}

// edit

fun FORM.editBeliefStatusHistory(
    state: State,
    history: History<BeliefStatus>,
    startDate: Date?,
) = selectHistory(state, BELIEVE, history, "Belief Status", startDate, null, HtmlBlockTag::editBeliefStatus)

fun HtmlBlockTag.editBeliefStatus(
    state: State,
    param: String,
    status: BeliefStatus,
    start: Date?,
) {
    selectValue("Belief Status", param, BeliefStatusType.entries, status.getType())

    when (status) {
        Atheist, UndefinedBeliefStatus -> doNothing()
        is WorshipOfGod -> selectElement(state, combine(param, GOD), state.sortGods(), status.god)
        is WorshipOfPantheon -> selectElement(
            state,
            combine(param, PANTHEON),
            state.sortPantheons(),
            status.pantheon
        )
    }
}

// parse

fun parseBeliefStatusHistory(parameters: Parameters, state: State, startDate: Date?) =
    parseHistory(parameters, BELIEVE, state, startDate, ::parseBeliefStatus)

fun parseBeliefStatus(parameters: Parameters, state: State, param: String): BeliefStatus {
    return when (parse(parameters, param, BeliefStatusType.Undefined)) {
        BeliefStatusType.Undefined -> UndefinedBeliefStatus
        BeliefStatusType.Atheist -> Atheist
        BeliefStatusType.God -> WorshipOfGod(parseGodId(parameters, combine(param, GOD)))
        BeliefStatusType.Pantheon -> WorshipOfPantheon(parsePantheonId(parameters, combine(param, PANTHEON)))
    }
}