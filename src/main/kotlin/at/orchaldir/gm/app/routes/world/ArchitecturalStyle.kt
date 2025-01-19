package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.REVIVAL
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.world.parseArchitecturalStyle
import at.orchaldir.gm.core.action.CreateArchitecturalStyle
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle.*
import at.orchaldir.gm.core.model.world.building.ARCHITECTURAL_STYLE_TYPE
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.selector.sortArchitecturalStyles
import at.orchaldir.gm.core.selector.sortBuildings
import at.orchaldir.gm.core.selector.world.*
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

@Resource("/$ARCHITECTURAL_STYLE_TYPE")
class ArchitecturalStyleRoutes {
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
            logger.info { "Get details of architectural style ${details.id.value}" }

            val state = STORE.getState()
            val style = state.getArchitecturalStyleStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleDetails(call, state, style)
            }
        }
        get<ArchitecturalStyleRoutes.New> {
            logger.info { "Add new architectural style" }

            STORE.dispatch(CreateArchitecturalStyle)

            call.respondRedirect(
                call.application.href(
                    ArchitecturalStyleRoutes.Edit(
                        STORE.getState().getArchitecturalStyleStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<ArchitecturalStyleRoutes.Delete> { delete ->
            logger.info { "Delete architectural style ${delete.id.value}" }

            STORE.dispatch(DeleteArchitecturalStyle(delete.id))

            call.respondRedirect(call.application.href(ArchitecturalStyleRoutes.All()))

            STORE.getState().save()
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
            val style = parseArchitecturalStyle(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, state, style)
            }
        }
        post<ArchitecturalStyleRoutes.Update> { update ->
            logger.info { "Update architectural style ${update.id.value}" }

            val style = parseArchitecturalStyle(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateArchitecturalStyle(style))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllArchitecturalStyles(call: ApplicationCall, state: State, sort: SortArchitecturalStyle) {
    val styles = STORE.getState().sortArchitecturalStyles(sort)
    val createLink = call.application.href(ArchitecturalStyleRoutes.New())
    val sortNameLink = call.application.href(ArchitecturalStyleRoutes.All())
    val sortStartLink = call.application.href(ArchitecturalStyleRoutes.All(Start))
    val sortEndLink = call.application.href(ArchitecturalStyleRoutes.All(End))

    simpleHtml("Architectural Styles") {
        field("Count", styles.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortStartLink, "Start")
            +" "
            link(sortEndLink, "End")
        }
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
                    td { link(call, style) }
                    td { showDate(call, state, style.start) }
                    td { showOptionalDate(call, state, style.end) }
                    td { style.revival?.let { link(call, state, it) } }
                    td { countBuildings(state, style) }
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HtmlBlockTag.countBuildings(state: State, style: ArchitecturalStyle) {
    val count = state.getBuildings(style.id).size

    if (count > 0) {
        +count.toString()
    }
}

private fun HTML.showArchitecturalStyleDetails(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val revivedBy = state.getRevivedBy(style.id)
    val backLink = call.application.href(ArchitecturalStyleRoutes.All())
    val deleteLink = call.application.href(ArchitecturalStyleRoutes.Delete(style.id))
    val editLink = call.application.href(ArchitecturalStyleRoutes.Edit(style.id))

    simpleHtml("Architectural Style: ${style.name}") {
        field("Name", style.name)
        field(call, state, "Start", style.start)
        optionalField(call, state, "End", style.end)
        if (style.revival != null) {
            fieldLink("Revival of", call, state, style.revival)
        }
        showList("Revived by", revivedBy) { s ->
            link(call, s)
        }
        showList("Buildings", state.sortBuildings()) { (building, name) ->
            link(call, building.id, name)
        }
        action(editLink, "Edit")
        if (state.canDelete(style.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showArchitecturalStyleEditor(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val minDate = state.getEarliestBuilding(state.getBuildings(style.id))?.constructionDate
    val backLink = href(call, style.id)
    val previewLink = call.application.href(ArchitecturalStyleRoutes.Preview(style.id))
    val updateLink = call.application.href(ArchitecturalStyleRoutes.Update(style.id))

    simpleHtml("Edit Architectural Style: ${style.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(style.name)
            selectYear(state, "Start", style.start, START, null, minDate)
            selectOptionalYear(state, "End", style.end, END, style.start.nextYear())
            selectOptionalValue(
                "Revival Of",
                REVIVAL,
                state.getArchitecturalStyleStorage().getOptional(style.revival),
                state.getPossibleStylesForRevival(style),
                false,
            ) { s ->
                label = s.name()
                value = s.id().value.toString()
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}
