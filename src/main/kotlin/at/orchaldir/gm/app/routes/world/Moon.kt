package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.html.world.editMoon
import at.orchaldir.gm.app.html.world.parseMoon
import at.orchaldir.gm.app.html.world.showMoon
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortMoon
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.selector.util.sortMoons
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$MOON_TYPE")
class MoonRoutes {
    @Resource("all")
    class All(
        val sort: SortMoon = SortMoon.Name,
        val parent: MoonRoutes = MoonRoutes(),
    )

    @Resource("details")
    class Details(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("new")
    class New(val parent: MoonRoutes = MoonRoutes())

    @Resource("delete")
    class Delete(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("edit")
    class Edit(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("preview")
    class Preview(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("update")
    class Update(val id: MoonId, val parent: MoonRoutes = MoonRoutes())
}

fun Application.configureMoonRouting() {
    routing {
        get<MoonRoutes.All> { all ->
            logger.info { "Get all moons" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMoons(call, STORE.getState(), all.sort)
            }
        }
        get<MoonRoutes.Details> { details ->
            logger.info { "Get details of moon ${details.id.value}" }

            val state = STORE.getState()
            val moon = state.getMoonStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonDetails(call, state, moon)
            }
        }
        get<MoonRoutes.New> {
            handleCreateElement(STORE.getState().getMoonStorage()) { id ->
                MoonRoutes.Edit(id)
            }
        }
        get<MoonRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MoonRoutes.All())
        }
        get<MoonRoutes.Edit> { edit ->
            logger.info { "Get editor for moon ${edit.id.value}" }

            val state = STORE.getState()
            val moon = state.getMoonStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonEditor(call, state, moon)
            }
        }
        post<MoonRoutes.Preview> { preview ->
            logger.info { "Get preview for moon ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val moon = parseMoon(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonEditor(call, state, moon)
            }
        }
        post<MoonRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMoon)
        }
    }
}

private fun HTML.showAllMoons(
    call: ApplicationCall,
    state: State,
    sort: SortMoon = SortMoon.Name,
) {
    val moons = state.sortMoons(sort)
    val createLink = call.application.href(MoonRoutes.New())

    simpleHtml("Moons") {
        field("Count", moons.size)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Position" }
                th { +"Duration" }
                th { +"Color" }
                thMultiLines(listOf("Associated", "Plane"))
            }
            moons.forEach { moon ->
                tr {
                    tdLink(call, state, moon)
                    tdString(moon.title)
                    td { showPosition(call, state, moon.position, false) }
                    td { +"${moon.getCycle()} days" }
                    td { showOptionalColor(moon.color) }
                    tdLink(call, state, moon.plane)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showMoonDetails(
    call: ApplicationCall,
    state: State,
    moon: Moon,
) {
    val backLink = call.application.href(MoonRoutes.All())
    val deleteLink = call.application.href(MoonRoutes.Delete(moon.id))
    val editLink = call.application.href(MoonRoutes.Edit(moon.id))

    simpleHtmlDetails(moon) {
        showMoon(call, state, moon)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showMoonEditor(
    call: ApplicationCall,
    state: State,
    moon: Moon,
) {
    val backLink = href(call, moon.id)
    val previewLink = call.application.href(MoonRoutes.Preview(moon.id))
    val updateLink = call.application.href(MoonRoutes.Update(moon.id))

    simpleHtmlEditor(moon) {
        formWithPreview(previewLink, updateLink, backLink) {
            editMoon(state, moon)
        }
    }
}
