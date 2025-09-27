package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editTown
import at.orchaldir.gm.app.html.realm.parseRealm
import at.orchaldir.gm.app.html.realm.parseTown
import at.orchaldir.gm.app.html.realm.showTown
import at.orchaldir.gm.app.html.util.displayVitalStatus
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TOWN_TYPE
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.SortTown
import at.orchaldir.gm.core.selector.character.countResident
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.countBuildings
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TOWN_TYPE")
class TownRoutes {
    @Resource("all")
    class All(
        val sort: SortTown = SortTown.Name,
        val parent: TownRoutes = TownRoutes(),
    )

    @Resource("details")
    class Details(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("new")
    class New(val parent: TownRoutes = TownRoutes())

    @Resource("delete")
    class Delete(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("edit")
    class Edit(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("preview")
    class Preview(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("update")
    class Update(val id: TownId, val parent: TownRoutes = TownRoutes())
}

fun Application.configureTownRouting() {
    routing {
        get<TownRoutes.All> { all ->
            logger.info { "Get all towns" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTowns(call, STORE.getState(), all.sort)
            }
        }
        get<TownRoutes.Details> { details ->
            logger.info { "Get details of town ${details.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownDetails(call, state, town)
            }
        }
        get<TownRoutes.New> {
            handleCreateElement(STORE.getState().getTownStorage()) { id ->
                TownRoutes.Edit(id)
            }
        }
        get<TownRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteTown(delete.id), TownRoutes())
        }
        get<TownRoutes.Edit> { edit ->
            logger.info { "Get editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownEditor(call, state, town)
            }
        }
        post<TownRoutes.Preview> { preview ->
            logger.info { "Preview town ${preview.id.value}" }

            val state = STORE.getState()
            val town = parseTown(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownEditor(call, state, town)
            }
        }
        post<TownRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTown)
        }
    }
}

private fun HTML.showAllTowns(
    call: ApplicationCall,
    state: State,
    sort: SortTown,
) {
    val towns = state.sortTowns(sort)
    val createLink = call.application.href(TownRoutes.New())

    simpleHtml("Towns") {
        field("Count", towns.size)
        showSortTableLinks(call, SortTown.entries, TownRoutes(), TownRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Founder" }
                thMultiLines(listOf("Founding", "Date"))
                thMultiLines(listOf("End", "Date"))
                th { +"End" }
                th { +"Owner" }
                th { +"Map" }
                th { +"Buildings" }
                th { +"Population" }
                th { +"Characters" }
            }
            towns.forEach { town ->
                tr {
                    tdLink(call, state, town)
                    tdString(town.title)
                    td { showReference(call, state, town.founder, false) }
                    td { showOptionalDate(call, state, town.startDate()) }
                    td { showOptionalDate(call, state, town.endDate()) }
                    td { displayVitalStatus(call, state, town.status) }
                    tdLink(call, state, town.owner.current)
                    tdLink(call, state, state.getCurrentTownMap(town.id)?.id)
                    tdSkipZero(state.countBuildings(town.id))
                    tdSkipZero(town.population.getTotalPopulation())
                    tdSkipZero(state.countResident(town.id))
                }
            }
        }
        showCreatorCount(call, state, towns, "Founders")
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTownDetails(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val backLink = call.application.href(TownRoutes.All())
    val deleteLink = call.application.href(TownRoutes.Delete(town.id))
    val editLink = call.application.href(TownRoutes.Edit(town.id))

    simpleHtmlDetails(town) {
        showTown(call, state, town)

        action(editLink, "Edit Town")
        action(deleteLink, "Delete")
        back(backLink)
    }
}


private fun HTML.showTownEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.Preview(town.id))
    val updateLink = call.application.href(TownRoutes.Update(town.id))

    simpleHtmlEditor(town) {
        formWithPreview(previewLink, updateLink, backLink) {
            editTown(call, state, town)
        }
    }
}
