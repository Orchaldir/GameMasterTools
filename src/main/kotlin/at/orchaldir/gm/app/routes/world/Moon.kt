package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.world.editMoon
import at.orchaldir.gm.app.html.world.parseMoon
import at.orchaldir.gm.app.html.world.showMoon
import at.orchaldir.gm.app.routes.*
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
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$MOON_TYPE")
class MoonRoutes : Routes<MoonId, SortMoon> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMoon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MoonId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MoonId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureMoonRouting() {
    routing {
        get<MoonRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MoonRoutes(),
                state.sortMoons(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    createPositionColumn(call, state),
                    tdColumn("Duration") { +"${it.getCycle()} days" },
                    tdColumn("Color") { showOptionalColor(it.color) },
                    Column(listOf("Associated", "Plane")) { tdLink(call, state, it.plane) },
                ),
            )
        }
        get<MoonRoutes.Details> { details ->
            handleShowElement(details.id, MoonRoutes(), HtmlBlockTag::showMoon)
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
