package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.*
import at.orchaldir.gm.app.html.showMultiLine
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.MELEE_WEAPON_TYPE
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponId
import at.orchaldir.gm.core.model.util.SortMeleeWeapon
import at.orchaldir.gm.core.selector.util.sortMeleeWeapons
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MELEE_WEAPON_TYPE")
class MeleeWeaponRoutes : Routes<MeleeWeaponId, SortMeleeWeapon> {
    @Resource("all")
    class All(
        val sort: SortMeleeWeapon = SortMeleeWeapon.Name,
        val parent: MeleeWeaponRoutes = MeleeWeaponRoutes(),
    )

    @Resource("details")
    class Details(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("new")
    class New(val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("delete")
    class Delete(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("edit")
    class Edit(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("preview")
    class Preview(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    @Resource("update")
    class Update(val id: MeleeWeaponId, val parent: MeleeWeaponRoutes = MeleeWeaponRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMeleeWeapon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MeleeWeaponId) = call.application.href(Edit(id))
}

fun Application.configureMeleeWeaponRouting() {
    routing {
        get<MeleeWeaponRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MeleeWeaponRoutes(),
                state.sortMeleeWeapons(all.sort),
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
                ),
            )
        }
        get<MeleeWeaponRoutes.Details> { details ->
            handleShowElement(details.id, MeleeWeaponRoutes(), HtmlBlockTag::showMeleeWeapon)
        }
        get<MeleeWeaponRoutes.New> {
            handleCreateElement(STORE.getState().getMeleeWeaponStorage()) { id ->
                MeleeWeaponRoutes.Edit(id)
            }
        }
        get<MeleeWeaponRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MeleeWeaponRoutes.All())
        }
        get<MeleeWeaponRoutes.Edit> { edit ->
            handleEditElement(edit.id, MeleeWeaponRoutes(), HtmlBlockTag::editMeleeWeapon)
        }
        post<MeleeWeaponRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, MeleeWeaponRoutes(), ::parseMeleeWeapon, HtmlBlockTag::editMeleeWeapon)
        }
        post<MeleeWeaponRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMeleeWeapon)
        }
    }
}
