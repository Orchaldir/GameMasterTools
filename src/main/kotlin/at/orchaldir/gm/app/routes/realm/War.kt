package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.realm.editWar
import at.orchaldir.gm.app.html.model.realm.parseWar
import at.orchaldir.gm.app.html.model.realm.showWar
import at.orchaldir.gm.app.html.model.showOptionalDate
import at.orchaldir.gm.core.action.CreateWar
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.WAR_TYPE
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.util.SortWar
import at.orchaldir.gm.core.selector.character.countKilledCharacters
import at.orchaldir.gm.core.selector.realm.canDeleteWar
import at.orchaldir.gm.core.selector.realm.countBattles
import at.orchaldir.gm.core.selector.realm.countDestroyedRealms
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortWars
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

@Resource("/$WAR_TYPE")
class WarRoutes {
    @Resource("all")
    class All(
        val sort: SortWar = SortWar.Name,
        val parent: WarRoutes = WarRoutes(),
    )

    @Resource("details")
    class Details(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("new")
    class New(val parent: WarRoutes = WarRoutes())

    @Resource("delete")
    class Delete(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("edit")
    class Edit(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("preview")
    class Preview(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("update")
    class Update(val id: WarId, val parent: WarRoutes = WarRoutes())
}

fun Application.configureWarRouting() {
    routing {
        get<WarRoutes.All> { all ->
            logger.info { "Get all wars" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllWars(call, STORE.getState(), all.sort)
            }
        }
        get<WarRoutes.Details> { details ->
            logger.info { "Get details of war ${details.id.value}" }

            val state = STORE.getState()
            val war = state.getWarStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showWarDetails(call, state, war)
            }
        }
        get<WarRoutes.New> {
            logger.info { "Add new war" }

            STORE.dispatch(CreateWar)

            call.respondRedirect(
                call.application.href(
                    WarRoutes.Edit(
                        STORE.getState().getWarStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<WarRoutes.Delete> { delete ->
            logger.info { "Delete war ${delete.id.value}" }

            STORE.dispatch(DeleteWar(delete.id))

            call.respondRedirect(call.application.href(WarRoutes.All()))

            STORE.getState().save()
        }
        get<WarRoutes.Edit> { edit ->
            logger.info { "Get editor for war ${edit.id.value}" }

            val state = STORE.getState()
            val war = state.getWarStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showWarEditor(call, state, war)
            }
        }
        post<WarRoutes.Preview> { preview ->
            logger.info { "Get preview for war ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val war = parseWar(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showWarEditor(call, state, war)
            }
        }
        post<WarRoutes.Update> { update ->
            logger.info { "Update war ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val war = parseWar(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateWar(war))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllWars(
    call: ApplicationCall,
    state: State,
    sort: SortWar,
) {
    val calendar = state.getDefaultCalendar()
    val wars = state.sortWars(sort)
    val createLink = call.application.href(WarRoutes.New())

    simpleHtml("Wars") {
        field("Count", wars.size)
        showSortTableLinks(call, SortWar.entries, WarRoutes(), WarRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"End" }
                th { +"Years" }
                th { +"Battles" }
                thMultiLines(listOf("Destroyed", "Realms"))
                thMultiLines(listOf("Killed", "Characters"))
            }
            wars.forEach { war ->
                tr {
                    tdLink(call, state, war)
                    td { showOptionalDate(call, state, war.startDate) }
                    td { showOptionalDate(call, state, war.endDate) }
                    tdSkipZero(calendar.getYears(war.getDuration(state)))
                    tdSkipZero(state.countBattles(war.id))
                    tdSkipZero(state.countDestroyedRealms(war.id))
                    tdSkipZero(state.countKilledCharacters(war.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showWarDetails(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    val backLink = call.application.href(WarRoutes.All())
    val deleteLink = call.application.href(WarRoutes.Delete(war.id))
    val editLink = call.application.href(WarRoutes.Edit(war.id))

    simpleHtmlDetails(war) {
        showWar(call, state, war)

        action(editLink, "Edit")

        if (state.canDeleteWar(war.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showWarEditor(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    val backLink = href(call, war.id)
    val previewLink = call.application.href(WarRoutes.Preview(war.id))
    val updateLink = call.application.href(WarRoutes.Update(war.id))

    simpleHtmlEditor(war) {
        formWithPreview(previewLink, updateLink, backLink) {
            editWar(state, war)
        }
    }
}
