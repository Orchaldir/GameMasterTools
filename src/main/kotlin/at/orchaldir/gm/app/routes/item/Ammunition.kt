package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createIdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.item.ammunition.editAmmunition
import at.orchaldir.gm.app.html.item.ammunition.parseAmmunition
import at.orchaldir.gm.app.html.item.ammunition.showAmmunition
import at.orchaldir.gm.app.html.showInlineIds
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.core.model.item.ammunition.AMMUNITION_TYPE
import at.orchaldir.gm.core.model.item.ammunition.AmmunitionId
import at.orchaldir.gm.core.model.util.SortAmmunition
import at.orchaldir.gm.core.selector.util.sortAmmunition
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$AMMUNITION_TYPE")
class AmmunitionRoutes : Routes<AmmunitionId, SortAmmunition> {
    @Resource("all")
    class All(
        val sort: SortAmmunition = SortAmmunition.Name,
        val parent: AmmunitionRoutes = AmmunitionRoutes(),
    )

    @Resource("details")
    class Details(val id: AmmunitionId, val parent: AmmunitionRoutes = AmmunitionRoutes())

    @Resource("new")
    class New(val parent: AmmunitionRoutes = AmmunitionRoutes())

    @Resource("delete")
    class Delete(val id: AmmunitionId, val parent: AmmunitionRoutes = AmmunitionRoutes())

    @Resource("edit")
    class Edit(val id: AmmunitionId, val parent: AmmunitionRoutes = AmmunitionRoutes())

    @Resource("preview")
    class Preview(val id: AmmunitionId, val parent: AmmunitionRoutes = AmmunitionRoutes())

    @Resource("update")
    class Update(val id: AmmunitionId, val parent: AmmunitionRoutes = AmmunitionRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortAmmunition) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: AmmunitionId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: AmmunitionId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: AmmunitionId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: AmmunitionId) = call.application.href(Update(id))
}

fun Application.configureAmmunitionRouting() {
    routing {
        get<AmmunitionRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                AmmunitionRoutes(),
                state.sortAmmunition(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createIdColumn(call, state, "Type") { it.type },
                    tdColumn("Modifiers") {
                        showInlineIds(call, state, it.modifiers)
                    }
                ),
            )
        }
        get<AmmunitionRoutes.Details> { details ->
            handleShowElement(details.id, AmmunitionRoutes(), HtmlBlockTag::showAmmunition)
        }
        get<AmmunitionRoutes.New> {
            handleCreateElement(AmmunitionRoutes(), STORE.getState().getAmmunitionStorage())
        }
        get<AmmunitionRoutes.Delete> { delete ->
            handleDeleteElement(AmmunitionRoutes(), delete.id)
        }
        get<AmmunitionRoutes.Edit> { edit ->
            handleEditElement(edit.id, AmmunitionRoutes(), HtmlBlockTag::editAmmunition)
        }
        post<AmmunitionRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                AmmunitionRoutes(),
                ::parseAmmunition,
                HtmlBlockTag::editAmmunition
            )
        }
        post<AmmunitionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseAmmunition)
        }
    }
}
