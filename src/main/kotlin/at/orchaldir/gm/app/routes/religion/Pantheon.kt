package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editPantheon
import at.orchaldir.gm.app.html.religion.parsePantheon
import at.orchaldir.gm.app.html.religion.showPantheon
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.religion.PANTHEON_TYPE
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.util.SortPantheon
import at.orchaldir.gm.core.selector.util.sortPantheons
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PANTHEON_TYPE")
class PantheonRoutes : Routes<PantheonId, SortPantheon> {
    @Resource("all")
    class All(
        val sort: SortPantheon = SortPantheon.Name,
        val parent: PantheonRoutes = PantheonRoutes(),
    )

    @Resource("details")
    class Details(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("new")
    class New(val parent: PantheonRoutes = PantheonRoutes())

    @Resource("delete")
    class Delete(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("edit")
    class Edit(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("preview")
    class Preview(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("update")
    class Update(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPantheon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PantheonId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PantheonId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PantheonId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PantheonId) = call.application.href(Update(id))
}

fun Application.configurePantheonRouting() {
    routing {
        get<PantheonRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PantheonRoutes(),
                state.sortPantheons(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    Column("Gods") { tdSkipZero(it.gods) },
                    Column("Believers") { tdBelievers(state.getCharacterStorage(), it.id) },
                    Column("Organization") { tdBelievers(state.getOrganizationStorage(), it.id) },
                ),
            )
        }
        get<PantheonRoutes.Details> { details ->
            handleShowElement(details.id, PantheonRoutes(), HtmlBlockTag::showPantheon)
        }
        get<PantheonRoutes.New> {
            handleCreateElement(PantheonRoutes(), STORE.getState().getPantheonStorage())
        }
        get<PantheonRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PantheonRoutes())
        }
        get<PantheonRoutes.Edit> { edit ->
            handleEditElement(edit.id, PantheonRoutes(), HtmlBlockTag::editPantheon)
        }
        post<PantheonRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, PantheonRoutes(), ::parsePantheon, HtmlBlockTag::editPantheon)
        }
        post<PantheonRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePantheon)
        }
    }
}
