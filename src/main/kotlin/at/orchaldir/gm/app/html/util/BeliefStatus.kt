package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.BELIEVE
import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PANTHEON
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.religion.parsePantheonId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.getBelievers
import at.orchaldir.gm.core.selector.util.getFormerBelievers
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showBeliefStatusHistory(
    call: ApplicationCall,
    state: State,
    history: History<BeliefStatus>,
) = showHistory(call, state, history, "Belief Status", HtmlBlockTag::showBeliefStatus)

fun HtmlBlockTag.fieldBeliefStatus(
    call: ApplicationCall,
    state: State,
    status: BeliefStatus,
) = field("Belief Status") {
    showBeliefStatus(call, state, status)
}

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

fun <ID : Id<ID>> HtmlBlockTag.showCurrentAndFormerBelievers(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    val characters = getBelievers(state.getCharacterStorage(), id)
    val formerCharacters = getFormerBelievers(state.getCharacterStorage(), id) - characters
    val organizations = getBelievers(state.getOrganizationStorage(), id)
    val formerOrganizations = getFormerBelievers(state.getOrganizationStorage(), id) - organizations
    val templates = getBelievers(state.getCharacterTemplateStorage(), id)

    if (characters.isEmpty() && formerCharacters.isEmpty() && organizations.isEmpty() && formerOrganizations.isEmpty() && templates.isEmpty()) {
        return
    }

    h2 { +"Believers" }

    fieldElements(call, state, characters)
    fieldElements(call, state, "Characters in the Past", formerCharacters)
    fieldElements(call, state, organizations)
    fieldElements(call, state, "Organizations in the Past", formerOrganizations)
    fieldElements(call, state, templates)
}

// edit

fun HtmlBlockTag.editBeliefStatusHistory(
    state: State,
    history: History<BeliefStatus>,
    startDate: Date?,
) = selectHistory(state, BELIEVE, history, "Belief Status", startDate, null, HtmlBlockTag::editBeliefStatus)

fun HtmlBlockTag.selectBeliefStatus(
    state: State,
    param: String,
    status: BeliefStatus,
) {
    showDetails("Belief Status", true) {
        editBeliefStatus(state, param, status, null)
    }
}

private fun HtmlBlockTag.editBeliefStatus(
    state: State,
    param: String,
    status: BeliefStatus,
    start: Date?,
) {
    selectValue("Type", param, BeliefStatusType.entries, status.getType())

    when (status) {
        Atheist, UndefinedBeliefStatus -> doNothing()
        is WorshipOfGod -> selectElement(state, combine(param, GOD), state.getGodStorage().getAll(), status.god)
        is WorshipOfPantheon -> selectElement(
            state,
            combine(param, PANTHEON),
            state.getPantheonStorage().getAll(),
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