package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.editDamageType
import at.orchaldir.gm.app.html.rpg.combat.parseDamageType
import at.orchaldir.gm.app.html.rpg.combat.showDamageType
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.DAMAGE_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.util.SortDamageType
import at.orchaldir.gm.core.selector.rpg.getArmorTypes
import at.orchaldir.gm.core.selector.rpg.getMeleeWeaponTypes
import at.orchaldir.gm.core.selector.util.sortDamageTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$DAMAGE_TYPE_TYPE")
class DamageTypeRoutes : Routes<DamageTypeId, SortDamageType> {
    @Resource("all")
    class All(
        val sort: SortDamageType = SortDamageType.Name,
        val parent: DamageTypeRoutes = DamageTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("new")
    class New(val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("delete")
    class Delete(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("edit")
    class Edit(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("preview")
    class Preview(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    @Resource("update")
    class Update(val id: DamageTypeId, val parent: DamageTypeRoutes = DamageTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDamageType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DamageTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DamageTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: DamageTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: DamageTypeId) = call.application.href(Update(id))
}

fun Application.configureDamageTypeRouting() {
    routing {
        get<DamageTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DamageTypeRoutes(),
                state.sortDamageTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Short") { tdString(it.short) },
                    countCollectionColumn("Armors") { state.getArmorTypes(it.id) },
                    countCollectionColumn("Melee Weapons") { state.getMeleeWeaponTypes(it.id) },
                ),
            )
        }
        get<DamageTypeRoutes.Details> { details ->
            handleShowElement(details.id, DamageTypeRoutes(), HtmlBlockTag::showDamageType)
        }
        get<DamageTypeRoutes.New> {
            handleCreateElement(DamageTypeRoutes(), STORE.getState().getDamageTypeStorage())
        }
        get<DamageTypeRoutes.Delete> { delete ->
            handleDeleteElement(DamageTypeRoutes(), delete.id)
        }
        get<DamageTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, DamageTypeRoutes(), HtmlBlockTag::editDamageType)
        }
        post<DamageTypeRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, DamageTypeRoutes(), ::parseDamageType, HtmlBlockTag::editDamageType)
        }
        post<DamageTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDamageType)
        }
    }
}
