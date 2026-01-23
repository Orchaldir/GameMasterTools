package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.editAmmunitionType
import at.orchaldir.gm.app.html.rpg.combat.parseAmmunitionType
import at.orchaldir.gm.app.html.rpg.combat.showAmmunitionType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.AMMUNITION_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId
import at.orchaldir.gm.core.model.util.SortAmmunitionType
import at.orchaldir.gm.core.selector.item.ammunition.getAmmunition
import at.orchaldir.gm.core.selector.util.sortAmmunitionTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$AMMUNITION_TYPE_TYPE")
class AmmunitionTypeRoutes : Routes<AmmunitionTypeId, SortAmmunitionType> {
    @Resource("all")
    class All(
        val sort: SortAmmunitionType = SortAmmunitionType.Name,
        val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: AmmunitionTypeId, val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    @Resource("new")
    class New(val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    @Resource("delete")
    class Delete(val id: AmmunitionTypeId, val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    @Resource("edit")
    class Edit(val id: AmmunitionTypeId, val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    @Resource("preview")
    class Preview(val id: AmmunitionTypeId, val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    @Resource("update")
    class Update(val id: AmmunitionTypeId, val parent: AmmunitionTypeRoutes = AmmunitionTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortAmmunitionType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: AmmunitionTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: AmmunitionTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: AmmunitionTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: AmmunitionTypeId) = call.application.href(Update(id))
}

fun Application.configureAmmunitionTypeRouting() {
    routing {
        get<AmmunitionTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                AmmunitionTypeRoutes(),
                state.sortAmmunitionTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Variants") { state.getAmmunition(it.id) },
                ),
            )
        }
        get<AmmunitionTypeRoutes.Details> { details ->
            handleShowElement(details.id, AmmunitionTypeRoutes(), HtmlBlockTag::showAmmunitionType)
        }
        get<AmmunitionTypeRoutes.New> {
            handleCreateElement(AmmunitionTypeRoutes(), STORE.getState().getAmmunitionTypeStorage())
        }
        get<AmmunitionTypeRoutes.Delete> { delete ->
            handleDeleteElement(AmmunitionTypeRoutes(), delete.id)
        }
        get<AmmunitionTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, AmmunitionTypeRoutes(), HtmlBlockTag::editAmmunitionType)
        }
        post<AmmunitionTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                AmmunitionTypeRoutes(),
                ::parseAmmunitionType,
                HtmlBlockTag::editAmmunitionType
            )
        }
        post<AmmunitionTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseAmmunitionType)
        }
    }
}
