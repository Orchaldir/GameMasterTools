package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editStreet
import at.orchaldir.gm.app.html.world.parseStreet
import at.orchaldir.gm.app.html.world.showStreet
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortStreet
import at.orchaldir.gm.core.model.world.street.STREET_TYPE
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.util.sortStreets
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

@Resource("/$STREET_TYPE")
class StreetRoutes : Routes<StreetId> {
    @Resource("all")
    class All(
        val sort: SortStreet = SortStreet.Name,
        val parent: StreetRoutes = StreetRoutes(),
    )

    @Resource("details")
    class Details(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("new")
    class New(val parent: StreetRoutes = StreetRoutes())

    @Resource("delete")
    class Delete(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("edit")
    class Edit(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("update")
    class Update(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: StreetId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: StreetId) = call.application.href(Edit(id))
}

fun Application.configureStreetRouting() {
    routing {
        get<StreetRoutes.All> { all ->
            logger.info { "Get all traditions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllStreets(call, STORE.getState(), all.sort)
            }
        }
        get<StreetRoutes.Details> { details ->
            handleShowElement(details.id, StreetRoutes(), HtmlBlockTag::showStreet)
        }
        get<StreetRoutes.New> {
            handleCreateElement(STORE.getState().getStreetStorage()) { id ->
                StreetRoutes.Edit(id)
            }
        }
        get<StreetRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, StreetRoutes())
        }
        get<StreetRoutes.Edit> { edit ->
            logger.info { "Get editor for street ${edit.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, street)
            }
        }
        post<StreetRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStreet)
        }
    }
}

private fun HTML.showAllStreets(
    call: ApplicationCall,
    state: State,
    sort: SortStreet,
) {
    val streets = state.sortStreets(sort)
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Streets") {
        field("Count", streets.size)
        showList(streets) { street ->
            link(call, state, street)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    street: Street,
) {
    val backLink = href(call, street.id)
    val updateLink = call.application.href(StreetRoutes.Update(street.id))

    simpleHtmlEditor(street) {
        form {
            editStreet(state, street)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
