package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.*
import at.orchaldir.gm.app.html.showMultiLine
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.MELEE_WEAPON_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponTypeId
import at.orchaldir.gm.core.model.util.SortMeleeWeaponType
import at.orchaldir.gm.core.selector.item.getMeleeWeapons
import at.orchaldir.gm.core.selector.util.sortMeleeWeaponTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MELEE_WEAPON_TYPE_TYPE")
class MeleeWeaponTypeRoutes : Routes<MeleeWeaponTypeId, SortMeleeWeaponType> {
    @Resource("all")
    class All(
        val sort: SortMeleeWeaponType = SortMeleeWeaponType.Name,
        val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: MeleeWeaponTypeId, val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    @Resource("new")
    class New(val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    @Resource("delete")
    class Delete(val id: MeleeWeaponTypeId, val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    @Resource("edit")
    class Edit(val id: MeleeWeaponTypeId, val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    @Resource("preview")
    class Preview(val id: MeleeWeaponTypeId, val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    @Resource("update")
    class Update(val id: MeleeWeaponTypeId, val parent: MeleeWeaponTypeRoutes = MeleeWeaponTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMeleeWeaponType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MeleeWeaponTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MeleeWeaponTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MeleeWeaponTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MeleeWeaponTypeId) = call.application.href(Update(id))
}

fun Application.configureMeleeWeaponTypeRouting() {
    routing {
        get<MeleeWeaponTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MeleeWeaponTypeRoutes(),
                state.sortMeleeWeaponTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Damage") {
                        showMultiLine(it.attacks) { attack ->
                            displayAttackEffect(call, state, attack.effect)
                        }
                    },
                    tdColumn("Reach") {
                        showMultiLine(it.attacks) { attack ->
                            displayReach(attack.reach)
                        }
                    },
                    tdColumn("Parrying") {
                        showMultiLine(it.attacks) { attack ->
                            displayParrying(attack.parrying)
                        }
                    },
                    countCollectionColumn("Weapons") { state.getMeleeWeapons(it.id) },
                ),
            )
        }
        get<MeleeWeaponTypeRoutes.Details> { details ->
            handleShowElement(details.id, MeleeWeaponTypeRoutes(), HtmlBlockTag::showMeleeWeaponType)
        }
        get<MeleeWeaponTypeRoutes.New> {
            handleCreateElement(MeleeWeaponTypeRoutes(), STORE.getState().getMeleeWeaponTypeStorage())
        }
        get<MeleeWeaponTypeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MeleeWeaponTypeRoutes())
        }
        get<MeleeWeaponTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, MeleeWeaponTypeRoutes(), HtmlBlockTag::editMeleeWeaponType)
        }
        post<MeleeWeaponTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                MeleeWeaponTypeRoutes(),
                ::parseMeleeWeaponType,
                HtmlBlockTag::editMeleeWeaponType
            )
        }
        post<MeleeWeaponTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMeleeWeaponType)
        }
    }
}
