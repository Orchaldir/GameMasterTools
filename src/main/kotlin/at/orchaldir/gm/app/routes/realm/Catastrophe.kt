package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.realm.displayCauseOfCatastrophe
import at.orchaldir.gm.app.html.realm.editCatastrophe
import at.orchaldir.gm.app.html.realm.parseCatastrophe
import at.orchaldir.gm.app.html.realm.showCatastrophe
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.CATASTROPHE_TYPE
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.util.SortCatastrophe
import at.orchaldir.gm.core.selector.util.sortCatastrophes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$CATASTROPHE_TYPE")
class CatastropheRoutes : Routes<CatastropheId, SortCatastrophe> {
    @Resource("all")
    class All(
        val sort: SortCatastrophe = SortCatastrophe.Name,
        val parent: CatastropheRoutes = CatastropheRoutes(),
    )

    @Resource("details")
    class Details(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("new")
    class New(val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("delete")
    class Delete(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("edit")
    class Edit(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("preview")
    class Preview(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("update")
    class Update(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCatastrophe) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CatastropheId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CatastropheId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CatastropheId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CatastropheId) = call.application.href(Update(id))
}

fun Application.configureCatastropheRouting() {
    routing {
        get<CatastropheRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CatastropheRoutes(),
                state.sortCatastrophes(all.sort),
                listOf<Column<Catastrophe>>(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state, "Start"),
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state, "Years"),
                    tdColumn("Cause") { displayCauseOfCatastrophe(call, state, it.cause, false) }
                ) + createDestroyedColumns(state),
            )
        }
        get<CatastropheRoutes.Details> { details ->
            handleShowElement(details.id, CatastropheRoutes(), HtmlBlockTag::showCatastrophe)
        }
        get<CatastropheRoutes.New> {
            handleCreateElement(CatastropheRoutes(), STORE.getState().getCatastropheStorage())
        }
        get<CatastropheRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CatastropheRoutes())
        }
        get<CatastropheRoutes.Edit> { edit ->
            handleEditElement(edit.id, CatastropheRoutes(), HtmlBlockTag::editCatastrophe)
        }
        post<CatastropheRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, CatastropheRoutes(), ::parseCatastrophe, HtmlBlockTag::editCatastrophe)
        }
        post<CatastropheRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCatastrophe)
        }
    }
}
