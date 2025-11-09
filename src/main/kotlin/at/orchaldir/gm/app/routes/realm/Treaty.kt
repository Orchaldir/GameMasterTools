package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.realm.editTreaty
import at.orchaldir.gm.app.html.realm.parseTreaty
import at.orchaldir.gm.app.html.realm.showTreaty
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.TREATY_TYPE
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.util.SortTreaty
import at.orchaldir.gm.core.selector.util.sortTreaties
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$TREATY_TYPE")
class TreatyRoutes : Routes<TreatyId, SortTreaty> {
    @Resource("all")
    class All(
        val sort: SortTreaty = SortTreaty.Name,
        val parent: TreatyRoutes = TreatyRoutes(),
    )

    @Resource("details")
    class Details(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("new")
    class New(val parent: TreatyRoutes = TreatyRoutes())

    @Resource("delete")
    class Delete(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("edit")
    class Edit(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("preview")
    class Preview(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("update")
    class Update(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortTreaty) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: TreatyId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TreatyId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: TreatyId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: TreatyId) = call.application.href(Update(id))
}

fun Application.configureTreatyRouting() {
    routing {
        get<TreatyRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TreatyRoutes(),
                state.sortTreaties(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    countCollectionColumn("Participants", Treaty::participants)
                ),
            )
        }
        get<TreatyRoutes.Details> { details ->
            handleShowElement(details.id, TreatyRoutes(), HtmlBlockTag::showTreaty)
        }
        get<TreatyRoutes.New> {
            handleCreateElement(TreatyRoutes(), STORE.getState().getTreatyStorage())
        }
        get<TreatyRoutes.Delete> { delete ->
            handleDeleteElement(TreatyRoutes(), delete.id)
        }
        get<TreatyRoutes.Edit> { edit ->
            handleEditElement(edit.id, TreatyRoutes(), HtmlBlockTag::editTreaty)
        }
        post<TreatyRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, TreatyRoutes(), ::parseTreaty, HtmlBlockTag::editTreaty)
        }
        post<TreatyRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTreaty)
        }
    }
}
