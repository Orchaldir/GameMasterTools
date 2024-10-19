package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseMoon
import at.orchaldir.gm.core.action.CreateMoon
import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
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
import kotlinx.html.HTML
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/moons")
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
                showAllMoons(call)
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
                showMoonEditor(call, moon)
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

private fun HTML.showAllMoons(call: ApplicationCall) {
    val moons = STORE.getState().getMoonStorage().getAll().sortedBy { it.name }
    val count = moons.size
    val createLink = call.application.href(MoonRoutes.New())

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
    val nextNewMoon = moon.getNextNewMoon(state.time.currentDate)
    val nextFullMoon = moon.getNextFullMoon(state.time.currentDate)
    val backLink = call.application.href(MoonRoutes())
    val deleteLink = call.application.href(MoonRoutes.Delete(moon.id))
    val editLink = call.application.href(MoonRoutes.Edit(moon.id))

    simpleHtml("Moon: ${moon.name}") {
        field("Id", moon.id.value.toString())
        field("Name", moon.name)
        field("Cycle", moon.getCycle().toString() + " days")
        field("Color", moon.color.toString())
        if (nextNewMoon > nextFullMoon) {
            field(call, state, "Next Full Moon", nextFullMoon)
            field(call, state, "Next New Moon", nextNewMoon)
        } else {
            field(call, state, "Next New Moon", nextNewMoon)
            field(call, state, "Next Full Moon", nextFullMoon)
        }

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
    val updateLink = call.application.href(MoonRoutes.Update(moon.id))

    simpleHtml("Edit Moon: ${moon.name}") {
        field("Id", moon.id.value.toString())
        form {
            selectName(moon.name)
            selectInt("Days per Quarter", moon.daysPerQuarter, 1, 100, LENGTH, false)
            selectColor("Color", COLOR, OneOf(Color.entries), moon.color)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
