package at.orchaldir.gm.app.routes.rpg.equipment

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.rpg.combat.*
import at.orchaldir.gm.app.html.rpg.equipment.editEquipmentType
import at.orchaldir.gm.app.html.rpg.equipment.parseEquipmentType
import at.orchaldir.gm.app.html.rpg.equipment.showEquipmentType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.item.EquipmentRoutes
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

    @Resource("melee")
    class AllMeleeAttacks(
        val sort: SortEquipmentType = SortEquipmentType.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
    )

    @Resource("ranged")
    class AllRangedAttacks(
        val sort: SortEquipmentType = SortEquipmentType.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
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
    fun allMeleeAttacks(call: ApplicationCall, sort: SortEquipmentType) = call.application.href(AllMeleeAttacks(sort))
    fun allAllRangedAttacks(call: ApplicationCall, sort: SortEquipmentType) = call.application.href(AllRangedAttacks(sort))
    override fun delete(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: EquipmentTypeId) = call.application.href(Update(id))
}

fun Application.configureEquipmentTypeRouting() {
    routing {
        get<EquipmentTypeRoutes.All> { all ->
            val routes = EquipmentTypeRoutes()
            val state = STORE.getState()

            handleShowAllElements(
                routes,
                state.sortEquipmentTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Melee Attacks") { it.meleeAttacks },
                    countCollectionColumn("Ranged Attacks") { it.meleeAttacks },
                    tdColumn("Protection") { displayProtection(call, state, it.protection) },
                    createCostFactorColumn { it.cost },
                    createWeightColumn { getWeightOfType(it.weight) },
                    countCollectionColumn("Equipment") { state.getArmors(it.id) },
                ),
            ) {
                action(routes.allMeleeAttacks(call, all.sort), "All Melee Attacks")
                action(routes.allAllRangedAttacks(call, all.sort), "All Ranged Attacks")
            }
        }
        get<EquipmentTypeRoutes.AllMeleeAttacks> { all ->
            val routes = EquipmentTypeRoutes()
            val state = STORE.getState()

            handleShowAllElements(
                EquipmentTypeRoutes(),
                state.sortEquipmentTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Damage") {
                        showMultiLine(it.meleeAttacks) { attack ->
                            displayAttackEffect(call, state, attack.effect)
                        }
                    },
                    tdColumn("Reach") {
                        showMultiLine(it.meleeAttacks) { attack ->
                            displayReach(attack.reach)
                        }
                    },
                    tdColumn("Parrying") {
                        showMultiLine(it.meleeAttacks) { attack ->
                            displayParrying(attack.parrying)
                        }
                    },
                ),
            ) {
                action(routes.all(call, all.sort), "All")
            }
        }
        get<EquipmentTypeRoutes.AllRangedAttacks> { all ->
            val routes = EquipmentTypeRoutes()
            val state = STORE.getState()

            handleShowAllElements(
                EquipmentTypeRoutes(),
                state.sortEquipmentTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Damage") {
                        showMultiLine(it.rangedAttacks) { attack ->
                            displayAttackEffect(call, state, attack.effect)
                        }
                    },
                    tdColumn("Accuracy") {
                        showMultiLine(it.rangedAttacks) { attack ->
                            displayAccuracy(attack.accuracy)
                        }
                    },
                    tdColumn("Range") {
                        showMultiLine(it.rangedAttacks) { attack ->
                            displayRange(call, state, attack.range)
                        }
                    },
                    tdColumn("Shots") {
                        showMultiLine(it.rangedAttacks) { attack ->
                            displayShots(call, state, attack.shots)
                        }
                    },
                ),
            ) {
                action(routes.all(call, all.sort), "All")
            }
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
