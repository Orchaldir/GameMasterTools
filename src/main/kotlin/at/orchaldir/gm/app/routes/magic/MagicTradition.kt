package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createCreatorColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.magic.editMagicTradition
import at.orchaldir.gm.app.html.magic.parseMagicTradition
import at.orchaldir.gm.app.html.magic.showMagicTradition
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.magic.MAGIC_TRADITION_TYPE
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.util.sortMagicTraditions
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MAGIC_TRADITION_TYPE")
class MagicTraditionRoutes : Routes<MagicTraditionId, SortMagicTradition> {
    @Resource("all")
    class All(
        val sort: SortMagicTradition = SortMagicTradition.Name,
        val parent: MagicTraditionRoutes = MagicTraditionRoutes(),
    )

    @Resource("details")
    class Details(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("new")
    class New(val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("delete")
    class Delete(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("edit")
    class Edit(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("preview")
    class Preview(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("update")
    class Update(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMagicTradition) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Edit(id))
}

fun Application.configureMagicTraditionRouting() {
    routing {
        get<MagicTraditionRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MagicTraditionRoutes(),
                state.sortMagicTraditions(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createCreatorColumn(call, state, "Founder"),
                    countCollectionColumn("Groups", MagicTradition::groups),
                ),
            )
        }
        get<MagicTraditionRoutes.Details> { details ->
            handleShowElement(details.id, MagicTraditionRoutes(), HtmlBlockTag::showMagicTradition)
        }
        get<MagicTraditionRoutes.New> {
            handleCreateElement(STORE.getState().getMagicTraditionStorage()) { id ->
                MagicTraditionRoutes.Edit(id)
            }
        }
        get<MagicTraditionRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MagicTraditionRoutes.All())
        }
        get<MagicTraditionRoutes.Edit> { edit ->
            handleEditElement(edit.id, MagicTraditionRoutes(), HtmlBlockTag::editMagicTradition)
        }
        post<MagicTraditionRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, MagicTraditionRoutes(), ::parseMagicTradition, HtmlBlockTag::editMagicTradition)
        }
        post<MagicTraditionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMagicTradition)
        }
    }
}

