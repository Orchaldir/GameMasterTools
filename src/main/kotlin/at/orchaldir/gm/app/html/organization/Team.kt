package at.orchaldir.gm.app.html.team

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Team
import at.orchaldir.gm.core.model.organization.TeamId
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
    showCreated(call, state, team.id)
    showOwnedElements(call, state, team.id)
    showDataSources(call, state, team.sources)
}

// edit

fun FORM.editTeam(
    state: State,
    team: Team,
) {
    selectName(team.name)
    selectOptionalDate(state, "Date", team.date, DATE)
    selectCreator(state, team.founder, team.id, team.date, "Founder")
    editDataSources(state, team.sources)
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
        parseDataSources(parameters),
    )
}
