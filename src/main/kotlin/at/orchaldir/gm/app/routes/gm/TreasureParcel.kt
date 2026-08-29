package at.orchaldir.gm.app.routes.gm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.gm.treasure.editTreasureParcel
import at.orchaldir.gm.app.html.gm.treasure.parseTreasureParcel
import at.orchaldir.gm.app.html.gm.treasure.showTreasureParcel
import at.orchaldir.gm.app.html.tdEnum
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.gm.treasure.TREASURE_PARCEL_TYPE
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.model.util.SortTreasureParcel
import at.orchaldir.gm.core.selector.util.sortTreasureParcels
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$TREASURE_PARCEL_TYPE")
class TreasureParcelRoutes : Routes<TreasureParcelId, SortTreasureParcel> {
    @Resource("all")
    class All(
        val sort: SortTreasureParcel = SortTreasureParcel.Name,
        val parent: TreasureParcelRoutes = TreasureParcelRoutes(),
    )

    @Resource("details")
    class Details(val id: TreasureParcelId, val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    @Resource("new")
    class New(val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    @Resource("delete")
    class Delete(val id: TreasureParcelId, val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    @Resource("edit")
    class Edit(val id: TreasureParcelId, val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    @Resource("preview")
    class Preview(val id: TreasureParcelId, val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    @Resource("update")
    class Update(val id: TreasureParcelId, val parent: TreasureParcelRoutes = TreasureParcelRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortTreasureParcel) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: TreasureParcelId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TreasureParcelId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: TreasureParcelId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: TreasureParcelId) = call.application.href(Update(id))
}

fun Application.configureTreasureParcelRouting() {
    routing {
        get<TreasureParcelRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TreasureParcelRoutes(),
                state.sortTreasureParcels(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Type") { tdEnum(it.entry.getType()) },
                ),
            )
        }
        get<TreasureParcelRoutes.Details> { details ->
            handleShowElement(details.id, TreasureParcelRoutes(), HtmlBlockTag::showTreasureParcel)
        }
        get<TreasureParcelRoutes.New> {
            handleCreateElement(TreasureParcelRoutes(), STORE.getState().getTreasureParcelStorage())
        }
        get<TreasureParcelRoutes.Delete> { delete ->
            handleDeleteElement(TreasureParcelRoutes(), delete.id)
        }
        get<TreasureParcelRoutes.Edit> { edit ->
            handleEditElement(edit.id, TreasureParcelRoutes(), HtmlBlockTag::editTreasureParcel)
        }
        post<TreasureParcelRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                TreasureParcelRoutes(),
                ::parseTreasureParcel,
                HtmlBlockTag::editTreasureParcel
            )
        }
        post<TreasureParcelRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTreasureParcel)
        }
    }
}
