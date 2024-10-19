package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseArchitecturalStyle
import at.orchaldir.gm.core.action.CreateArchitecturalStyle
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
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

@Resource("/architectural_styles")
class ArchitecturalStyleRoutes {
    @Resource("details")
    class Details(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("new")
    class New(val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("delete")
    class Delete(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("edit")
    class Edit(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("update")
    class Update(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())
}

fun Application.configureArchitecturalStyleRouting() {
    routing {
        get<ArchitecturalStyleRoutes> {
            logger.info { "Get all architectural styles" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllArchitecturalStyles(call)
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

            call.respondRedirect(call.application.href(ArchitecturalStyleRoutes()))

            STORE.getState().save()
        }
        get<ArchitecturalStyleRoutes.Edit> { edit ->
            logger.info { "Get editor for architectural style ${edit.id.value}" }

            val state = STORE.getState()
            val style = state.getArchitecturalStyleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, style)
            }
        }
        post<ArchitecturalStyleRoutes.Update> { update ->
            logger.info { "Update architectural style ${update.id.value}" }

            val style = parseArchitecturalStyle(update.id, call.receiveParameters())

            STORE.dispatch(UpdateArchitecturalStyle(style))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllArchitecturalStyles(call: ApplicationCall) {
    val styles = STORE.getState().getArchitecturalStyleStorage().getAll().sortedBy { it.name }
    val count = styles.size
    val createLink = call.application.href(ArchitecturalStyleRoutes.New())

    simpleHtml("Architectural Styles") {
        field("Count", count.toString())
        showList(styles) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showArchitecturalStyleDetails(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val backLink = call.application.href(ArchitecturalStyleRoutes())
    val deleteLink = call.application.href(ArchitecturalStyleRoutes.Delete(style.id))
    val editLink = call.application.href(ArchitecturalStyleRoutes.Edit(style.id))

    simpleHtml("Architectural Style: ${style.name}") {
        field("Id", style.id.value.toString())
        field("Name", style.name)
        field(call, state, "From", style.startDate)
        optionalField(call, state, "Until", style.endDate)
        if (style.revival != null) {
            field("Revival od") {
                link(call, state, style.revival)
            }
        }

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showArchitecturalStyleEditor(
    call: ApplicationCall,
    style: ArchitecturalStyle,
) {
    val backLink = href(call, style.id)
    val updateLink = call.application.href(ArchitecturalStyleRoutes.Update(style.id))

    simpleHtml("Edit ArchitecturalStyle: ${style.name}") {
        field("Id", style.id.value.toString())
        form {
            selectName(style.name)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
