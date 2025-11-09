package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editTown
import at.orchaldir.gm.app.html.realm.parseTown
import at.orchaldir.gm.app.html.realm.showTown
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.TOWN_TYPE
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.SortTown
import at.orchaldir.gm.core.selector.character.countResidents
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.countBuildings
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$TOWN_TYPE")
class TownRoutes : Routes<TownId, SortTown> {
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
    override fun preview(call: ApplicationCall, id: TownId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: TownId) = call.application.href(Update(id))
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
                    Column("Title") { tdString(it.title) },
                    createCreatorColumn(call, state, "Founder"),
                    createStartDateColumn(call, state, "Founding"),
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state),
                    createVitalColumn(call, state),
                    createIdColumn(call, state, "Owner") { it.owner.current },
                    createIdColumn(call, state, "Map") { state.getCurrentTownMap(it.id)?.id },
                    countColumnForId("Buildings", state::countBuildings),
                    createPopulationColumn(),
                    countColumnForId("Residents", state::countResidents),
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
            }
        }
        get<TownRoutes.Details> { details ->
            handleShowElement(details.id, TownRoutes(), HtmlBlockTag::showTown)
        }
        get<TownRoutes.New> {
            handleCreateElement(TownRoutes(), STORE.getState().getTownStorage())
        }
        get<TownRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TownRoutes())
        }
        get<TownRoutes.Edit> { edit ->
            handleEditElement(edit.id, TownRoutes(), HtmlBlockTag::editTown)
        }
        post<TownRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, TownRoutes(), ::parseTown, HtmlBlockTag::editTown)
        }
        post<TownRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTown)
        }
    }
}
