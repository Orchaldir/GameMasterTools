package at.orchaldir.gm.app.html.team

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.MEMBER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Team
import at.orchaldir.gm.core.model.organization.TeamId
import at.orchaldir.gm.core.selector.util.sortCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTeam(
    call: ApplicationCall,
    state: State,
    team: Team,
) {
    optionalField(call, state, "Date", team.date)
    fieldReference(call, state, team.founder, "Founder")
    fieldIdList(call, state, "Members", team.members())
    fieldIdList(call, state, "Former Members", team.formerMembers())
    showDataSources(call, state, team.sources)
    showCreated(call, state, team.id)
    showOwnedElements(call, state, team.id)
}

// edit

fun FORM.editTeam(
    state: State,
    team: Team,
) {
    selectName(team.name)
    selectOptionalDate(state, "Date", team.date, DATE)
    selectCreator(state, team.founder, team.id, team.date, "Founder")
    editMembers(state, team)
    editDataSources(state, team.sources)
}

private fun FORM.editMembers(
    state: State,
    team: Team,
) {
    val characters = state.sortCharacters()

    selectElements(
        state,
        "Members",
        MEMBER,
        characters,
        team.members(),
    )
    selectElements(
        state,
        "Former Members",
        combine(HISTORY, MEMBER),
        characters.filter { !team.members().contains(it.id) },
        team.formerMembers(),
    )
}

// parse

fun parseTeamId(parameters: Parameters, param: String) = TeamId(parseInt(parameters, param))

fun parseTeam(parameters: Parameters, state: State, id: TeamId): Team {
    val date = parseOptionalDate(parameters, state, DATE)

    return Team(
        id,
        parseName(parameters),
        parseCreator(parameters),
        date,
        parseElements(parameters, MEMBER, ::parseCharacterId),
        parseElements(parameters, combine(HISTORY, MEMBER), ::parseCharacterId),
        parseDataSources(parameters),
    )
}
