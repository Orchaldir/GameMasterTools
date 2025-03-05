package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.html.model.religion.parsePantheonId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.util.*
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
        is WorshipsGod -> link(call, state, status.god)
        is WorshipsPantheon -> link(call, state, status.pantheon)
    }
}

// edit

fun FORM.editBeliefStatusHistory(
    state: State,
    history: History<BeliefStatus>,
    startDate: Date,
) = selectHistory(state, BELIEVE, history, startDate, "Belief Status", HtmlBlockTag::editBeliefStatus)

fun HtmlBlockTag.editBeliefStatus(
    state: State,
    param: String,
    status: BeliefStatus,
    start: Date?,
) {
    selectValue("Belief Status", param, BeliefStatusType.entries, status.getType(), true)

    when (status) {
        Atheist, UndefinedBeliefStatus -> doNothing()
        is WorshipsGod -> selectElement(state, "God", combine(param, GOD), state.sortGods(), status.god)
        is WorshipsPantheon -> selectElement(
            state,
            "Pantheon",
            combine(param, PANTHEON),
            state.sortPantheons(),
            status.pantheon
        )
    }
}

// parse

fun parseBeliefStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, BELIEVE, state, startDate, ::parseBeliefStatus)

fun parseBeliefStatus(parameters: Parameters, state: State, param: String): BeliefStatus {
    return when (parse(parameters, param, BeliefStatusType.Undefined)) {
        BeliefStatusType.Undefined -> UndefinedBeliefStatus
        BeliefStatusType.Atheist -> Atheist
        BeliefStatusType.God -> WorshipsGod(parseGodId(parameters, combine(param, GOD)))
        BeliefStatusType.Pantheon -> WorshipsPantheon(parsePantheonId(parameters, combine(param, PANTHEON)))
    }
}