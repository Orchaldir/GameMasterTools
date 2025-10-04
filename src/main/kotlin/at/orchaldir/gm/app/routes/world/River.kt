package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editRiver
import at.orchaldir.gm.app.html.world.parseRiver
import at.orchaldir.gm.app.html.world.showRiver
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortRiver
import at.orchaldir.gm.core.model.world.terrain.RIVER_TYPE
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.selector.util.sortRivers
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$RIVER_TYPE")
class RiverRoutes : Routes<RiverId> {
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

    @Resource("update")
    class Update(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: RiverId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RiverId) = call.application.href(Edit(id))
}

fun Application.configureRiverRouting() {
    routing {
        get<RiverRoutes.All> { all ->
            logger.info { "Get all rivers" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRivers(call, STORE.getState(), all.sort)
            }
        }
        get<RiverRoutes.Details> { details ->
            handleShowElement(details.id, RiverRoutes(), HtmlBlockTag::showRiver)
        }
        get<RiverRoutes.New> {
            handleCreateElement(STORE.getState().getRiverStorage()) { id ->
                RiverRoutes.Edit(id)
            }
        }
        get<RiverRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RiverRoutes())
        }
        get<RiverRoutes.Edit> { edit ->
            logger.info { "Get editor for river ${edit.id.value}" }

            val state = STORE.getState()
            val river = state.getRiverStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRiverEditor(call, state, river)
            }
        }
        post<RiverRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRiver)
        }
    }
}

private fun HTML.showAllRivers(
    call: ApplicationCall,
    state: State,
    sort: SortRiver,
) {
    val rivers = state.sortRivers(sort)
    val createLink = call.application.href(RiverRoutes.New())

    simpleHtml("Rivers") {
        field("Count", rivers.size)
        showList(rivers) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showRiverEditor(
    call: ApplicationCall,
    state: State,
    river: River,
) {
    val backLink = href(call, river.id)
    val updateLink = call.application.href(RiverRoutes.Update(river.id))

    simpleHtmlEditor(river) {
        form {
            editRiver(state, river)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
