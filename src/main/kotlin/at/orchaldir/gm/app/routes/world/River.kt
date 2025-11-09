package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdInlineElements
import at.orchaldir.gm.app.html.world.editRiver
import at.orchaldir.gm.app.html.world.parseRiver
import at.orchaldir.gm.app.html.world.showRiver
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortRiver
import at.orchaldir.gm.core.model.world.terrain.RIVER_TYPE
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.selector.util.sortRivers
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$RIVER_TYPE")
class RiverRoutes : Routes<RiverId, SortRiver> {
    @Resource("all")
    class All(
        val sort: SortRiver = SortRiver.Name,
        val parent: RiverRoutes = RiverRoutes(),
    )

    @Resource("details")
    class Details(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("new")
    class New(val parent: RiverRoutes = RiverRoutes())

    @Resource("delete")
    class Delete(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("edit")
    class Edit(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("preview")
    class Preview(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("update")
    class Update(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRiver) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: RiverId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RiverId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RiverId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RiverId) = call.application.href(Update(id))
}

fun Application.configureRiverRouting() {
    routing {
        get<RiverRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RiverRoutes(),
                state.sortRivers(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Towns") { tdInlineElements(call, state, state.getTowns(it.id)) }
                ),
            )
        }
        get<RiverRoutes.Details> { details ->
            handleShowElement(details.id, RiverRoutes(), HtmlBlockTag::showRiver)
        }
        get<RiverRoutes.New> {
            handleCreateElement(RiverRoutes(), STORE.getState().getRiverStorage())
        }
        get<RiverRoutes.Delete> { delete ->
            handleDeleteElement(RiverRoutes(), delete.id)
        }
        get<RiverRoutes.Edit> { edit ->
            handleEditElement(edit.id, RiverRoutes(), HtmlBlockTag::editRiver)
        }
        post<RiverRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, RiverRoutes(), ::parseRiver, HtmlBlockTag::editRiver)
        }
        post<RiverRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRiver)
        }
    }
}
