package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdInlineElements
import at.orchaldir.gm.app.html.world.editStreet
import at.orchaldir.gm.app.html.world.parseStreet
import at.orchaldir.gm.app.html.world.showStreet
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortStreet
import at.orchaldir.gm.core.model.world.street.STREET_TYPE
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.util.sortStreets
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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

    @Resource("preview")
    class Preview(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("update")
    class Update(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortStreet) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: StreetId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: StreetId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: StreetId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: StreetId) = call.application.href(Update(id))
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
            handleCreateElement(StreetRoutes(), STORE.getState().getStreetStorage())
        }
        get<StreetRoutes.Delete> { delete ->
            handleDeleteElement(StreetRoutes(), delete.id)
        }
        get<StreetRoutes.Edit> { edit ->
            handleEditElement(edit.id, StreetRoutes(), HtmlBlockTag::editStreet)
        }
        post<StreetRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, StreetRoutes(), ::parseStreet, HtmlBlockTag::editStreet)
        }
        post<StreetRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStreet)
        }
    }
}
