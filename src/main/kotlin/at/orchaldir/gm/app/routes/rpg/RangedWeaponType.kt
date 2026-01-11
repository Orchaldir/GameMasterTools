package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.*
import at.orchaldir.gm.app.html.showMultiLine
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.RANGED_WEAPON_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponTypeId
import at.orchaldir.gm.core.model.util.SortRangedWeaponType
import at.orchaldir.gm.core.selector.item.equipment.getRangedWeapons
import at.orchaldir.gm.core.selector.util.sortRangedWeaponTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$RANGED_WEAPON_TYPE_TYPE")
class RangedWeaponTypeRoutes : Routes<RangedWeaponTypeId, SortRangedWeaponType> {
    @Resource("all")
    class All(
        val sort: SortRangedWeaponType = SortRangedWeaponType.Name,
        val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: RangedWeaponTypeId, val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    @Resource("new")
    class New(val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    @Resource("delete")
    class Delete(val id: RangedWeaponTypeId, val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    @Resource("edit")
    class Edit(val id: RangedWeaponTypeId, val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    @Resource("preview")
    class Preview(val id: RangedWeaponTypeId, val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    @Resource("update")
    class Update(val id: RangedWeaponTypeId, val parent: RangedWeaponTypeRoutes = RangedWeaponTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRangedWeaponType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: RangedWeaponTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RangedWeaponTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RangedWeaponTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RangedWeaponTypeId) = call.application.href(Update(id))
}

fun Application.configureRangedWeaponTypeRouting() {
    routing {
        get<RangedWeaponTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RangedWeaponTypeRoutes(),
                state.sortRangedWeaponTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Damage") {
                        showMultiLine(it.attacks) { attack ->
                            displayAttackEffect(call, state, attack.effect)
                        }
                    },
                    tdColumn("Accuracy") {
                        showMultiLine(it.attacks) { attack ->
                            displayAccuracy(attack.accuracy)
                        }
                    },
                    tdColumn("Range") {
                        showMultiLine(it.attacks) { attack ->
                            displayRange(call, state, attack.range)
                        }
                    },
                    tdColumn("Shots") {
                        showMultiLine(it.attacks) { attack ->
                            displayShots(attack.shots)
                        }
                    },
                    countCollectionColumn("Equipment") { state.getRangedWeapons(it.id) },
                ),
            )
        }
        get<RangedWeaponTypeRoutes.Details> { details ->
            handleShowElement(details.id, RangedWeaponTypeRoutes(), HtmlBlockTag::showRangedWeaponType)
        }
        get<RangedWeaponTypeRoutes.New> {
            handleCreateElement(RangedWeaponTypeRoutes(), STORE.getState().getRangedWeaponTypeStorage())
        }
        get<RangedWeaponTypeRoutes.Delete> { delete ->
            handleDeleteElement(RangedWeaponTypeRoutes(), delete.id)
        }
        get<RangedWeaponTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, RangedWeaponTypeRoutes(), HtmlBlockTag::editRangedWeaponType)
        }
        post<RangedWeaponTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                RangedWeaponTypeRoutes(),
                ::parseRangedWeaponType,
                HtmlBlockTag::editRangedWeaponType
            )
        }
        post<RangedWeaponTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRangedWeaponType)
        }
    }
}
