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

fun HtmlBlockTag.showBelieveStatusHistory(
    call: ApplicationCall,
    state: State,
    history: History<BelieveStatus>,
) = showHistory(call, state, history, "Believe Status", HtmlBlockTag::showBelieveStatus)

fun HtmlBlockTag.showBelieveStatus(
    call: ApplicationCall,
    state: State,
    status: BelieveStatus,
    showUndefined: Boolean = true,
) {
    when (status) {
        UndefinedBelieveStatus -> if (showUndefined) {
            +"Undefined"
        }

        is WorshipsGod -> link(call, state, status.god)
        is WorshipsPantheon -> link(call, state, status.pantheon)
    }
}

// edit

fun FORM.editBelieveStatusHistory(
    state: State,
    history: History<BelieveStatus>,
    startDate: Date,
) = selectHistory(state, BELIEVE, history, startDate, "Believe Status", HtmlBlockTag::editBelieveStatus)

fun HtmlBlockTag.editBelieveStatus(
    state: State,
    param: String,
    status: BelieveStatus,
    start: Date?,
) {
    selectValue("Believe Status", param, BelieveStatusType.entries, status.getType(), true)

    when (status) {
        UndefinedBelieveStatus -> doNothing()
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

fun parseBelieveStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, BELIEVE, state, startDate, ::parseBelieveStatus)

fun parseBelieveStatus(parameters: Parameters, state: State, param: String): BelieveStatus {
    return when (parse(parameters, param, BelieveStatusType.Undefined)) {
        BelieveStatusType.Undefined -> UndefinedBelieveStatus
        BelieveStatusType.God -> WorshipsGod(parseGodId(parameters, combine(param, GOD)))
        BelieveStatusType.Pantheon -> WorshipsPantheon(parsePantheonId(parameters, combine(param, PANTHEON)))
    }
}