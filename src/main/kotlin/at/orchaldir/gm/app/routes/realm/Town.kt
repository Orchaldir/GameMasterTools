package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.createAgeColumn
import at.orchaldir.gm.app.html.createCreatorColumn
import at.orchaldir.gm.app.html.createEndDateColumn
import at.orchaldir.gm.app.html.createIdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createPopulationColumn
import at.orchaldir.gm.app.html.createSkipZeroColumnForId
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.createStringColumn
import at.orchaldir.gm.app.html.createVitalColumn
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.realm.editTown
import at.orchaldir.gm.app.html.realm.parseTown
import at.orchaldir.gm.app.html.realm.showTown
import at.orchaldir.gm.app.html.showCreatorCount
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
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
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TOWN_TYPE")
class TownRoutes : Routes<TownId,SortTown> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortTown) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: TownId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TownId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureTownRouting() {
    routing {
        get<TownRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TownRoutes(),
                state.sortTowns(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStringColumn("Title") { it.title?.text },
                    createCreatorColumn(call, state, "Founder"),
                    createStartDateColumn(call, state, "Founding"),
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state),
                    createVitalColumn(call, state),
                    createIdColumn(call, state, "Owner") { it.owner.current },
                    createIdColumn(call, state, "Map") { state.getCurrentTownMap(it.id)?.id },
                    createSkipZeroColumnForId("Buildings", state::countBuildings),
                    createPopulationColumn(),
                    createSkipZeroColumnForId("Residents", state::countResident),
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
            }
        }
        get<TownRoutes.Details> { details ->
            handleShowElement(details.id, TownRoutes(), HtmlBlockTag::showTown)
        }
        get<TownRoutes.New> {
            handleCreateElement(STORE.getState().getTownStorage()) { id ->
                TownRoutes.Edit(id)
            }
        }
        get<TownRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TownRoutes.All())
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
