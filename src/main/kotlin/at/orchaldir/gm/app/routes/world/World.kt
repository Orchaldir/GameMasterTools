package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editWorld
import at.orchaldir.gm.app.html.world.parseWorld
import at.orchaldir.gm.app.html.world.showWorld
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortWorld
import at.orchaldir.gm.core.model.world.WORLD_TYPE
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.selector.util.getMoonsOf
import at.orchaldir.gm.core.selector.util.getRegionsIn
import at.orchaldir.gm.core.selector.util.sortWorlds
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$WORLD_TYPE")
class WorldRoutes : Routes<WorldId, SortWorld> {
    @Resource("all")
    class All(
        val sort: SortWorld = SortWorld.Name,
        val parent: WorldRoutes = WorldRoutes(),
    )

    @Resource("details")
    class Details(val id: WorldId, val parent: WorldRoutes = WorldRoutes())

    @Resource("new")
    class New(val parent: WorldRoutes = WorldRoutes())

    @Resource("delete")
    class Delete(val id: WorldId, val parent: WorldRoutes = WorldRoutes())

    @Resource("edit")
    class Edit(val id: WorldId, val parent: WorldRoutes = WorldRoutes())

    @Resource("preview")
    class Preview(val id: WorldId, val parent: WorldRoutes = WorldRoutes())

    @Resource("update")
    class Update(val id: WorldId, val parent: WorldRoutes = WorldRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortWorld) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: WorldId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: WorldId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: WorldId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: WorldId) = call.application.href(Update(id))
}

fun Application.configureWorldRouting() {
    routing {
        get<WorldRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                WorldRoutes(),
                state.sortWorlds(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    createPositionColumn(call, state),
                    countCollectionColumn("Moons") { state.getMoonsOf(it.id) },
                    countCollectionColumn("Regions") { state.getRegionsIn(it.id) },
                ),
            )
        }
        get<WorldRoutes.Details> { details ->
            handleShowElement(details.id, WorldRoutes(), HtmlBlockTag::showWorld)
        }
        get<WorldRoutes.New> {
            handleCreateElement(WorldRoutes(), STORE.getState().getWorldStorage())
        }
        get<WorldRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, WorldRoutes.All())
        }
        get<WorldRoutes.Edit> { edit ->
            handleEditElement(edit.id, WorldRoutes(), HtmlBlockTag::editWorld)
        }
        post<WorldRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, WorldRoutes(), ::parseWorld, HtmlBlockTag::editWorld)
        }
        post<WorldRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseWorld)
        }
    }
}
