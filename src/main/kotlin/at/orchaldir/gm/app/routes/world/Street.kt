package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editStreet
import at.orchaldir.gm.app.html.world.parseStreet
import at.orchaldir.gm.app.html.world.showStreet
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortStreet
import at.orchaldir.gm.core.model.world.street.STREET_TYPE
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.util.sortStreets
import at.orchaldir.gm.core.selector.world.getTowns
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
class StreetRoutes : Routes<StreetId, SortStreet> {
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
    override fun all(call: ApplicationCall, sort: SortStreet) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: StreetId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: StreetId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureStreetRouting() {
    routing {
        get<StreetRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                StreetRoutes(),
                state.sortStreets(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Towns") { tdInlineElements(call, state, state.getTowns(it.id)) }
                ),
            )
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
