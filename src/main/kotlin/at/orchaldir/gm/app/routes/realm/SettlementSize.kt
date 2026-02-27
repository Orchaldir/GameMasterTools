package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.realm.editSettlementSize
import at.orchaldir.gm.app.html.realm.parseSettlementSize
import at.orchaldir.gm.app.html.realm.showSettlementSize
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.SETTLEMENT_SIZE_TYPE
import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.SortSettlementSize
import at.orchaldir.gm.core.selector.realm.getSettlements
import at.orchaldir.gm.core.selector.util.sortSettlementSizes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$SETTLEMENT_SIZE_TYPE")
class SettlementSizeRoutes : Routes<SettlementSizeId, SortSettlementSize> {
    @Resource("all")
    class All(
        val sort: SortSettlementSize = SortSettlementSize.MaxPopulation,
        val parent: SettlementSizeRoutes = SettlementSizeRoutes(),
    )

    @Resource("details")
    class Details(val id: SettlementSizeId, val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    @Resource("new")
    class New(val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    @Resource("delete")
    class Delete(val id: SettlementSizeId, val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    @Resource("edit")
    class Edit(val id: SettlementSizeId, val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    @Resource("preview")
    class Preview(val id: SettlementSizeId, val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    @Resource("update")
    class Update(val id: SettlementSizeId, val parent: SettlementSizeRoutes = SettlementSizeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortSettlementSize) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: SettlementSizeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SettlementSizeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: SettlementSizeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: SettlementSizeId) = call.application.href(Update(id))
}

fun Application.configureSettlementSizeRouting() {
    routing {
        get<SettlementSizeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                SettlementSizeRoutes(),
                state.sortSettlementSizes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Max Population") { tdSkipZero(it.maxPopulation) },
                    countCollectionColumn("Settlements") { state.getSettlements(it.id) },
                ),
            )
        }
        get<SettlementSizeRoutes.Details> { details ->
            handleShowElement(details.id, SettlementSizeRoutes(), HtmlBlockTag::showSettlementSize)
        }
        get<SettlementSizeRoutes.New> {
            handleCreateElement(SettlementSizeRoutes(), STORE.getState().getSettlementSizeStorage())
        }
        get<SettlementSizeRoutes.Delete> { delete ->
            handleDeleteElement(SettlementSizeRoutes(), delete.id)
        }
        get<SettlementSizeRoutes.Edit> { edit ->
            handleEditElement(edit.id, SettlementSizeRoutes(), HtmlBlockTag::editSettlementSize)
        }
        post<SettlementSizeRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, SettlementSizeRoutes(), ::parseSettlementSize, HtmlBlockTag::editSettlementSize)
        }
        post<SettlementSizeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseSettlementSize)
        }
    }
}
