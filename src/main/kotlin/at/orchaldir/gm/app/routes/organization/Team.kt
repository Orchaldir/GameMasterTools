package at.orchaldir.gm.app.routes.team

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.team.editTeam
import at.orchaldir.gm.app.html.team.parseTeam
import at.orchaldir.gm.app.html.team.showTeam
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.core.action.CreateTeam
import at.orchaldir.gm.core.action.DeleteTeam
import at.orchaldir.gm.core.action.UpdateTeam
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.TEAM_TYPE
import at.orchaldir.gm.core.model.organization.Team
import at.orchaldir.gm.core.model.organization.TeamId
import at.orchaldir.gm.core.model.util.SortTeam
import at.orchaldir.gm.core.selector.organization.canDeleteTeam
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.core.selector.util.sortTeams
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TEAM_TYPE")
class TeamRoutes {
    @Resource("all")
    class All(
        val sort: SortTeam = SortTeam.Name,
        val parent: TeamRoutes = TeamRoutes(),
    )

    @Resource("details")
    class Details(val id: TeamId, val parent: TeamRoutes = TeamRoutes())

    @Resource("new")
    class New(val parent: TeamRoutes = TeamRoutes())

    @Resource("delete")
    class Delete(val id: TeamId, val parent: TeamRoutes = TeamRoutes())

    @Resource("edit")
    class Edit(val id: TeamId, val parent: TeamRoutes = TeamRoutes())

    @Resource("preview")
    class Preview(val id: TeamId, val parent: TeamRoutes = TeamRoutes())

    @Resource("update")
    class Update(val id: TeamId, val parent: TeamRoutes = TeamRoutes())
}

fun Application.configureTeamRouting() {
    routing {
        get<TeamRoutes.All> { all ->
            logger.info { "Get all teams" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTeams(call, STORE.getState(), all.sort)
            }
        }
        get<TeamRoutes.Details> { details ->
            logger.info { "Get details of team ${details.id.value}" }

            val state = STORE.getState()
            val team = state.getTeamStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTeamDetails(call, state, team)
            }
        }
        get<TeamRoutes.New> {
            logger.info { "Add new team" }

            STORE.dispatch(CreateTeam)

            call.respondRedirect(
                call.application.href(
                    TeamRoutes.Edit(
                        STORE.getState().getTeamStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<TeamRoutes.Delete> { delete ->
            logger.info { "Delete team ${delete.id.value}" }

            STORE.dispatch(DeleteTeam(delete.id))

            call.respondRedirect(call.application.href(TeamRoutes.All()))

            STORE.getState().save()
        }
        get<TeamRoutes.Edit> { edit ->
            logger.info { "Get editor for team ${edit.id.value}" }

            val state = STORE.getState()
            val team = state.getTeamStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTeamEditor(call, state, team)
            }
        }
        post<TeamRoutes.Preview> { preview ->
            logger.info { "Get preview for team ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val team = parseTeam(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTeamEditor(call, state, team)
            }
        }
        post<TeamRoutes.Update> { update ->
            logger.info { "Update team ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val team = parseTeam(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateTeam(team))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllTeams(
    call: ApplicationCall,
    state: State,
    sort: SortTeam,
) {
    val teams = state.sortTeams(sort)
    val createLink = call.application.href(TeamRoutes.New())

    simpleHtml("Teams") {
        field("Count", teams.size)
        showSortTableLinks(call, SortTeam.entries, TeamRoutes(), TeamRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Age" }
                th { +"Founder" }
                th { +"Members" }
                thMultiLines(listOf("Former", "Members"))
            }
            teams.forEach { team ->
                tr {
                    tdLink(call, state, team)
                    td { showOptionalDate(call, state, team.date) }
                    tdSkipZero(state.getAgeInYears(team.date))
                    td { showReference(call, state, team.founder, false) }
                    tdSkipZero(team.members())
                    tdSkipZero(team.formerMembers())
                }
            }
        }

        showCreatorCount(call, state, teams, "Founders")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTeamDetails(
    call: ApplicationCall,
    state: State,
    team: Team,
) {
    val backLink = call.application.href(TeamRoutes.All())
    val deleteLink = call.application.href(TeamRoutes.Delete(team.id))
    val editLink = call.application.href(TeamRoutes.Edit(team.id))

    simpleHtmlDetails(team) {
        showTeam(call, state, team)

        action(editLink, "Edit")

        if (state.canDeleteTeam(team.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showTeamEditor(
    call: ApplicationCall,
    state: State,
    team: Team,
) {
    val backLink = href(call, team.id)
    val previewLink = call.application.href(TeamRoutes.Preview(team.id))
    val updateLink = call.application.href(TeamRoutes.Update(team.id))

    simpleHtmlEditor(team) {
        formWithPreview(previewLink, updateLink, backLink) {
            editTeam(state, team)
        }
    }
}
