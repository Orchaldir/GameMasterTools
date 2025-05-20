package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editMoon
import at.orchaldir.gm.app.html.world.parseMoon
import at.orchaldir.gm.app.html.world.showMoon
import at.orchaldir.gm.core.action.CreateMoon
import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
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

@Resource("/$MOON_TYPE")
class MoonRoutes {
    @Resource("details")
    class Details(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("new")
    class New(val parent: MoonRoutes = MoonRoutes())

    @Resource("delete")
    class Delete(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("edit")
    class Edit(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("update")
    class Update(val id: MoonId, val parent: MoonRoutes = MoonRoutes())
}

fun Application.configureMoonRouting() {
    routing {
        get<MoonRoutes> {
            logger.info { "Get all moons" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMoons(call, STORE.getState())
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
            logger.info { "Add new moon" }

            STORE.dispatch(CreateMoon)

            call.respondRedirect(call.application.href(MoonRoutes.Edit(STORE.getState().getMoonStorage().lastId)))

            STORE.getState().save()
        }
        get<MoonRoutes.Delete> { delete ->
            logger.info { "Delete moon ${delete.id.value}" }

            STORE.dispatch(DeleteMoon(delete.id))

            call.respondRedirect(call.application.href(MoonRoutes()))

            STORE.getState().save()
        }
        get<MoonRoutes.Edit> { edit ->
            logger.info { "Get editor for moon ${edit.id.value}" }

            val state = STORE.getState()
            val moon = state.getMoonStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonEditor(call, state, moon)
            }
        }
        post<MoonRoutes.Update> { update ->
            logger.info { "Update moon ${update.id.value}" }

            val moon = parseMoon(update.id, call.receiveParameters())

            STORE.dispatch(UpdateMoon(moon))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMoons(
    call: ApplicationCall,
    state: State,
) {
    val moons = STORE.getState().getMoonStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(MoonRoutes.New())

    simpleHtml("Moons") {
        field("Count", moons.size)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Duration" }
                th { +"Color" }
                th { +"Plane" }
            }
            moons.forEach { moon ->
                tr {
                    tdLink(call, state, moon)
                    tdString(moon.title)
                    td { +"${moon.getCycle()} days" }
                    td { showColor(moon.color) }
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
    val backLink = call.application.href(MoonRoutes())
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
    val updateLink = call.application.href(MoonRoutes.Update(moon.id))

    simpleHtmlEditor(moon) {
        form {
            editMoon(state, moon)

            button("Update", updateLink)
        }
        back(backLink)
    }
}
