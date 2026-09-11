package at.orchaldir.gm.app.routes.rpg.equipment

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createCostFactorColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createWeightColumn
import at.orchaldir.gm.app.html.rpg.combat.displayProtection
import at.orchaldir.gm.app.html.rpg.equipment.editEquipmentType
import at.orchaldir.gm.app.html.rpg.equipment.parseEquipmentType
import at.orchaldir.gm.app.html.rpg.equipment.showEquipmentType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.equipment.EQUIPMENT_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentTypeId
import at.orchaldir.gm.core.model.util.SortEquipmentType
import at.orchaldir.gm.core.selector.item.equipment.getArmors
import at.orchaldir.gm.core.selector.item.equipment.getWeightOfType
import at.orchaldir.gm.core.selector.util.sortEquipmentTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$EQUIPMENT_TYPE_TYPE")
class EquipmentTypeRoutes : Routes<EquipmentTypeId, SortEquipmentType> {
    @Resource("all")
    class All(
        val sort: SortEquipmentType = SortEquipmentType.Name,
        val parent: EquipmentTypeRoutes = EquipmentTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: EquipmentTypeId, val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    @Resource("new")
    class New(val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    @Resource("delete")
    class Delete(val id: EquipmentTypeId, val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    @Resource("edit")
    class Edit(val id: EquipmentTypeId, val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    @Resource("preview")
    class Preview(val id: EquipmentTypeId, val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    @Resource("update")
    class Update(val id: EquipmentTypeId, val parent: EquipmentTypeRoutes = EquipmentTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortEquipmentType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Update(id))
}

fun Application.configureEquipmentTypeRouting() {
    routing {
        get<EquipmentTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                EquipmentTypeRoutes(),
                state.sortEquipmentTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Protection") { displayProtection(call, state, it.protection) },
                    createCostFactorColumn { it.cost },
                    createWeightColumn { getWeightOfType(it.weight) },
                    countCollectionColumn("Equipment") { state.getArmors(it.id) },
                ),
            )
        }
        get<EquipmentTypeRoutes.Details> { details ->
            handleShowElement(details.id, EquipmentTypeRoutes(), HtmlBlockTag::showEquipmentType)
        }
        get<EquipmentTypeRoutes.New> {
            handleCreateElement(EquipmentTypeRoutes(), STORE.getState().getEquipmentTypeStorage())
        }
        get<EquipmentTypeRoutes.Delete> { delete ->
            handleDeleteElement(EquipmentTypeRoutes(), delete.id)
        }
        get<EquipmentTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, EquipmentTypeRoutes(), HtmlBlockTag::editEquipmentType)
        }
        post<EquipmentTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                EquipmentTypeRoutes(),
                ::parseEquipmentType,
                HtmlBlockTag::editEquipmentType
            )
        }
        post<EquipmentTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseEquipmentType)
        }
    }
}
