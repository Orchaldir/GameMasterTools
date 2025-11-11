package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.displayProtection
import at.orchaldir.gm.app.html.rpg.combat.editShieldType
import at.orchaldir.gm.app.html.rpg.combat.parseShieldType
import at.orchaldir.gm.app.html.rpg.combat.showShieldType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.SHIELD_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.ShieldTypeId
import at.orchaldir.gm.core.model.util.SortShieldType
import at.orchaldir.gm.core.selector.item.getShields
import at.orchaldir.gm.core.selector.util.sortShieldTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$SHIELD_TYPE_TYPE")
class ShieldTypeRoutes : Routes<ShieldTypeId, SortShieldType> {
    @Resource("all")
    class All(
        val sort: SortShieldType = SortShieldType.Name,
        val parent: ShieldTypeRoutes = ShieldTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: ShieldTypeId, val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    @Resource("new")
    class New(val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    @Resource("delete")
    class Delete(val id: ShieldTypeId, val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    @Resource("edit")
    class Edit(val id: ShieldTypeId, val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    @Resource("preview")
    class Preview(val id: ShieldTypeId, val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    @Resource("update")
    class Update(val id: ShieldTypeId, val parent: ShieldTypeRoutes = ShieldTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortShieldType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ShieldTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ShieldTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: ShieldTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ShieldTypeId) = call.application.href(Update(id))
}

fun Application.configureShieldTypeRouting() {
    routing {
        get<ShieldTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ShieldTypeRoutes(),
                state.sortShieldTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Protection") { displayProtection(call, state, it.protection) },
                    countCollectionColumn("Equipment") { state.getShields(it.id) },
                ),
            )
        }
        get<ShieldTypeRoutes.Details> { details ->
            handleShowElement(details.id, ShieldTypeRoutes(), HtmlBlockTag::showShieldType)
        }
        get<ShieldTypeRoutes.New> {
            handleCreateElement(ShieldTypeRoutes(), STORE.getState().getShieldTypeStorage())
        }
        get<ShieldTypeRoutes.Delete> { delete ->
            handleDeleteElement(ShieldTypeRoutes(), delete.id)
        }
        get<ShieldTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, ShieldTypeRoutes(), HtmlBlockTag::editShieldType)
        }
        post<ShieldTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                ShieldTypeRoutes(),
                ::parseShieldType,
                HtmlBlockTag::editShieldType
            )
        }
        post<ShieldTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseShieldType)
        }
    }
}
