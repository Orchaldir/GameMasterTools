package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.world.editArchitecturalStyle
import at.orchaldir.gm.app.html.world.parseArchitecturalStyle
import at.orchaldir.gm.app.html.world.showArchitecturalStyle
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle.Name
import at.orchaldir.gm.core.model.world.building.ARCHITECTURAL_STYLE_TYPE
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.selector.util.sortArchitecturalStyles
import at.orchaldir.gm.core.selector.world.getBuildings
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

@Resource("/$ARCHITECTURAL_STYLE_TYPE")
class ArchitecturalStyleRoutes : Routes<ArchitecturalStyleId> {
    @Resource("all")
    class All(
        val sort: SortArchitecturalStyle = Name,
        val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes(),
    )

    @Resource("details")
    class Details(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("new")
    class New(val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("delete")
    class Delete(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("edit")
    class Edit(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("preview")
    class Preview(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("update")
    class Update(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: ArchitecturalStyleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ArchitecturalStyleId) = call.application.href(Edit(id))
}

fun Application.configureArchitecturalStyleRouting() {
    routing {
        get<ArchitecturalStyleRoutes.All> { all ->
            logger.info { "Get all architectural styles" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllArchitecturalStyles(call, STORE.getState(), all.sort)
            }
        }
        get<ArchitecturalStyleRoutes.Details> { details ->
            handleShowElement(details.id, ArchitecturalStyleRoutes(), HtmlBlockTag::showArchitecturalStyle)
        }
        get<ArchitecturalStyleRoutes.New> {
            handleCreateElement(STORE.getState().getArchitecturalStyleStorage()) { id ->
                ArchitecturalStyleRoutes.Edit(id)
            }
        }
        get<ArchitecturalStyleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ArchitecturalStyleRoutes.All())
        }
        get<ArchitecturalStyleRoutes.Edit> { edit ->
            logger.info { "Get editor for architectural style ${edit.id.value}" }

            val state = STORE.getState()
            val style = state.getArchitecturalStyleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, state, style)
            }
        }
        post<ArchitecturalStyleRoutes.Preview> { preview ->
            logger.info { "Get preview for architectural style ${preview.id.value}" }

            val state = STORE.getState()
            val style = parseArchitecturalStyle(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, state, style)
            }
        }
        post<ArchitecturalStyleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArchitecturalStyle)
        }
    }
}

private fun HTML.showAllArchitecturalStyles(call: ApplicationCall, state: State, sort: SortArchitecturalStyle) {
    val styles = STORE.getState().sortArchitecturalStyles(sort)
    val createLink = call.application.href(ArchitecturalStyleRoutes.New())

    simpleHtml("Architectural Styles") {
        field("Count", styles.size)
        showSortTableLinks(
            call,
            SortArchitecturalStyle.entries,
            ArchitecturalStyleRoutes(),
            ArchitecturalStyleRoutes::All
        )

        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"End" }
                th { +"Revival Of" }
                th { +"Buildings" }
            }
            styles.forEach { style ->
                tr {
                    tdLink(call, state, style)
                    td { showOptionalDate(call, state, style.start) }
                    td { showOptionalDate(call, state, style.end) }
                    tdLink(call, state, style.revival)
                    tdSkipZero(state.getBuildings(style.id))
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showArchitecturalStyleEditor(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val backLink = href(call, style.id)
    val previewLink = call.application.href(ArchitecturalStyleRoutes.Preview(style.id))
    val updateLink = call.application.href(ArchitecturalStyleRoutes.Update(style.id))

    simpleHtmlEditor(style) {
        formWithPreview(previewLink, updateLink, backLink) {
            editArchitecturalStyle(state, style)
        }
    }
}
