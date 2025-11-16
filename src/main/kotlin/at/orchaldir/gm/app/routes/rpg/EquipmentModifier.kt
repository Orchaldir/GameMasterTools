package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.editEquipmentModifier
import at.orchaldir.gm.app.html.rpg.combat.parseEquipmentModifier
import at.orchaldir.gm.app.html.rpg.combat.showEquipmentModifier
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.EQUIPMENT_MODIFIER_TYPE
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.model.util.SortArmorModifier
import at.orchaldir.gm.core.selector.item.getArmors
import at.orchaldir.gm.core.selector.util.sortEquipmentModifiers
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$EQUIPMENT_MODIFIER_TYPE")
class EquipmentModifierRoutes : Routes<EquipmentModifierId, SortArmorModifier> {
    @Resource("all")
    class All(
        val sort: SortArmorModifier = SortArmorModifier.Name,
        val parent: EquipmentModifierRoutes = EquipmentModifierRoutes(),
    )

    @Resource("details")
    class Details(val id: EquipmentModifierId, val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    @Resource("new")
    class New(val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    @Resource("delete")
    class Delete(val id: EquipmentModifierId, val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    @Resource("edit")
    class Edit(val id: EquipmentModifierId, val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    @Resource("preview")
    class Preview(val id: EquipmentModifierId, val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    @Resource("update")
    class Update(val id: EquipmentModifierId, val parent: EquipmentModifierRoutes = EquipmentModifierRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortArmorModifier) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: EquipmentModifierId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EquipmentModifierId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: EquipmentModifierId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: EquipmentModifierId) = call.application.href(Update(id))
}

fun Application.configureArmorModifierRouting() {
    routing {
        get<EquipmentModifierRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                EquipmentModifierRoutes(),
                state.sortEquipmentModifiers(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Armors") { state.getArmors(it.id) },
                ),
            )
        }
        get<EquipmentModifierRoutes.Details> { details ->
            handleShowElement(details.id, EquipmentModifierRoutes(), HtmlBlockTag::showEquipmentModifier)
        }
        get<EquipmentModifierRoutes.New> {
            handleCreateElement(EquipmentModifierRoutes(), STORE.getState().getEquipmentModifierStorage())
        }
        get<EquipmentModifierRoutes.Delete> { delete ->
            handleDeleteElement(EquipmentModifierRoutes(), delete.id)
        }
        get<EquipmentModifierRoutes.Edit> { edit ->
            handleEditElement(edit.id, EquipmentModifierRoutes(), HtmlBlockTag::editEquipmentModifier)
        }
        post<EquipmentModifierRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                EquipmentModifierRoutes(),
                ::parseEquipmentModifier,
                HtmlBlockTag::editEquipmentModifier
            )
        }
        post<EquipmentModifierRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseEquipmentModifier)
        }
    }
}
