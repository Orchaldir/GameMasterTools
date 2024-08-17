package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.NAME
import at.orchaldir.gm.app.parse.parseMoon
import at.orchaldir.gm.core.action.CreateMoon
import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.moon.Moon
import at.orchaldir.gm.core.model.moon.MoonId
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

@Resource("/moons")
class Moons {
    @Resource("details")
    class Details(val id: MoonId, val parent: Moons = Moons())

    @Resource("new")
    class New(val parent: Moons = Moons())

    @Resource("delete")
    class Delete(val id: MoonId, val parent: Moons = Moons())

    @Resource("edit")
    class Edit(val id: MoonId, val parent: Moons = Moons())

    @Resource("update")
    class Update(val id: MoonId, val parent: Moons = Moons())
}

fun Application.configureMoonRouting() {
    routing {
        get<Moons> {
            logger.info { "Get all moons" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMoons(call)
            }
        }
        get<Moons.Details> { details ->
            logger.info { "Get details of moon ${details.id.value}" }

            val state = STORE.getState()
            val moon = state.getMoonStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonDetails(call, state, moon)
            }
        }
        get<Moons.New> {
            logger.info { "Add new moon" }

            STORE.dispatch(CreateMoon)

            call.respondRedirect(call.application.href(Moons.Edit(STORE.getState().getMoonStorage().lastId)))

            STORE.getState().save()
        }
        get<Moons.Delete> { delete ->
            logger.info { "Delete moon ${delete.id.value}" }

            STORE.dispatch(DeleteMoon(delete.id))

            call.respondRedirect(call.application.href(Moons()))

            STORE.getState().save()
        }
        get<Moons.Edit> { edit ->
            logger.info { "Get editor for moon ${edit.id.value}" }

            val state = STORE.getState()
            val moon = state.getMoonStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMoonEditor(call, moon)
            }
        }
        post<Moons.Update> { update ->
            logger.info { "Update moon ${update.id.value}" }

            val moon = parseMoon(update.id, call.receiveParameters())

            STORE.dispatch(UpdateMoon(moon))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMoons(call: ApplicationCall) {
    val moons = STORE.getState().getMoonStorage().getAll().sortedBy { it.name }
    val count = moons.size
    val createLink = call.application.href(Moons.New())

    simpleHtml("Moons") {
        field("Count", count.toString())
        showList(moons) { nameList ->
            link(call, nameList)
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
    val backLink = call.application.href(Moons())
    val deleteLink = call.application.href(Moons.Delete(moon.id))
    val editLink = call.application.href(Moons.Edit(moon.id))

    simpleHtml("Moon: ${moon.name}") {
        field("Id", moon.id.value.toString())
        field("Name", moon.name)
        field("Duration", (moon.daysPerQuarter * 4).toString())
        field("Color", moon.color.toString())

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showMoonEditor(
    call: ApplicationCall,
    moon: Moon,
) {
    val backLink = href(call, moon.id)
    val updateLink = call.application.href(Moons.Update(moon.id))

    simpleHtml("Edit Moon: ${moon.name}") {
        field("Id", moon.id.value.toString())
        form {
            field("Name") {
                textInput(name = NAME) {
                    value = moon.name
                }
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}
