package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.editMeleeWeaponModifier
import at.orchaldir.gm.app.html.rpg.combat.parseMeleeWeaponModifier
import at.orchaldir.gm.app.html.rpg.combat.showMeleeWeaponModifier
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.MELEE_WEAPON_MODIFIER_TYPE
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifierId
import at.orchaldir.gm.core.model.util.SortMeleeWeaponModifier
import at.orchaldir.gm.core.selector.item.getMeleeWeapons
import at.orchaldir.gm.core.selector.util.sortMeleeWeaponModifiers
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MELEE_WEAPON_MODIFIER_TYPE")
class MeleeWeaponModifierRoutes : Routes<MeleeWeaponModifierId, SortMeleeWeaponModifier> {
    @Resource("all")
    class All(
        val sort: SortMeleeWeaponModifier = SortMeleeWeaponModifier.Name,
        val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes(),
    )

    @Resource("details")
    class Details(val id: MeleeWeaponModifierId, val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    @Resource("new")
    class New(val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    @Resource("delete")
    class Delete(val id: MeleeWeaponModifierId, val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    @Resource("edit")
    class Edit(val id: MeleeWeaponModifierId, val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    @Resource("preview")
    class Preview(val id: MeleeWeaponModifierId, val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    @Resource("update")
    class Update(val id: MeleeWeaponModifierId, val parent: MeleeWeaponModifierRoutes = MeleeWeaponModifierRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMeleeWeaponModifier) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MeleeWeaponModifierId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MeleeWeaponModifierId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MeleeWeaponModifierId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MeleeWeaponModifierId) = call.application.href(Update(id))
}

fun Application.configureMeleeWeaponModifierRouting() {
    routing {
        get<MeleeWeaponModifierRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MeleeWeaponModifierRoutes(),
                state.sortMeleeWeaponModifiers(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Weapons") { state.getMeleeWeapons(it.id) },
                ),
            )
        }
        get<MeleeWeaponModifierRoutes.Details> { details ->
            handleShowElement(details.id, MeleeWeaponModifierRoutes(), HtmlBlockTag::showMeleeWeaponModifier)
        }
        get<MeleeWeaponModifierRoutes.New> {
            handleCreateElement(STORE.getState().getMeleeWeaponModifierStorage()) { id ->
                MeleeWeaponModifierRoutes.Edit(id)
            }
        }
        get<MeleeWeaponModifierRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MeleeWeaponModifierRoutes.All())
        }
        get<MeleeWeaponModifierRoutes.Edit> { edit ->
            handleEditElement(edit.id, MeleeWeaponModifierRoutes(), HtmlBlockTag::editMeleeWeaponModifier)
        }
        post<MeleeWeaponModifierRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                MeleeWeaponModifierRoutes(),
                ::parseMeleeWeaponModifier,
                HtmlBlockTag::editMeleeWeaponModifier
            )
        }
        post<MeleeWeaponModifierRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMeleeWeaponModifier)
        }
    }
}
