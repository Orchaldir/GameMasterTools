package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editWorld
import at.orchaldir.gm.app.html.world.parseWorld
import at.orchaldir.gm.app.html.world.showWorld
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateWorld
import at.orchaldir.gm.core.action.DeleteWorld
import at.orchaldir.gm.core.action.UpdateWorld
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortWorld
import at.orchaldir.gm.core.model.world.WORLD_TYPE
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.WorldId
import at.orchaldir.gm.core.selector.util.getMoonsOf
import at.orchaldir.gm.core.selector.util.getRegionsIn
import at.orchaldir.gm.core.selector.util.sortWorlds
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
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$WORLD_TYPE")
class WorldRoutes {
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
}

fun Application.configureWorldRouting() {
    routing {
        get<WorldRoutes.All> { all ->
            logger.info { "Get all worlds" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllWorlds(call, STORE.getState(), all.sort)
            }
        }
        get<WorldRoutes.Details> { details ->
            logger.info { "Get details of world ${details.id.value}" }

            val state = STORE.getState()
            val world = state.getWorldStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showWorldDetails(call, state, world)
            }
        }
        get<WorldRoutes.New> {
            logger.info { "Add new world" }

            STORE.dispatch(CreateWorld)

            call.respondRedirect(call.application.href(WorldRoutes.Edit(STORE.getState().getWorldStorage().lastId)))

            STORE.getState().save()
        }
        get<WorldRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteWorld(delete.id), WorldRoutes())
        }
        get<WorldRoutes.Edit> { edit ->
            logger.info { "Get editor for world ${edit.id.value}" }

            val state = STORE.getState()
            val world = state.getWorldStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showWorldEditor(call, state, world)
            }
        }
        post<WorldRoutes.Preview> { preview ->
            logger.info { "Get preview for ${preview.id.print()}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val region = parseWorld(preview.id, formParameters)

            call.respondHtml(HttpStatusCode.OK) {
                showWorldEditor(call, state, region)
            }
        }
        post<WorldRoutes.Update> { update ->
            logger.info { "Update world ${update.id.value}" }

            val world = parseWorld(update.id, call.receiveParameters())

            STORE.dispatch(UpdateWorld(world))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllWorlds(
    call: ApplicationCall,
    state: State,
    sort: SortWorld = SortWorld.Name,
) {
    val worlds = state.sortWorlds(sort)
    val createLink = call.application.href(WorldRoutes.New())

    simpleHtml("Worlds") {
        field("Count", worlds.size)
        showSortTableLinks(call, SortWorld.entries, WorldRoutes(), WorldRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Moons" }
                th { +"Regions" }
            }
            worlds.forEach { world ->
                tr {
                    tdLink(call, state, world)
                    tdString(world.title)
                    tdSkipZero(state.getMoonsOf(world.id))
                    tdSkipZero(state.getRegionsIn(world.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showWorldDetails(
    call: ApplicationCall,
    state: State,
    world: World,
) {
    val backLink = call.application.href(WorldRoutes.All())
    val deleteLink = call.application.href(WorldRoutes.Delete(world.id))
    val editLink = call.application.href(WorldRoutes.Edit(world.id))

    simpleHtmlDetails(world) {
        showWorld(call, state, world)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showWorldEditor(
    call: ApplicationCall,
    state: State,
    world: World,
) {
    val backLink = href(call, world.id)
    val previewLink = call.application.href(WorldRoutes.Preview(world.id))
    val updateLink = call.application.href(WorldRoutes.Update(world.id))

    simpleHtmlEditor(world) {
        formWithPreview(previewLink, updateLink, backLink) {
            editWorld(state, world)
        }
    }
}
