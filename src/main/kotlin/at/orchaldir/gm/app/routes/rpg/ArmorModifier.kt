package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.editArmorModifier
import at.orchaldir.gm.app.html.rpg.combat.parseArmorModifier
import at.orchaldir.gm.app.html.rpg.combat.showArmorModifier
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.ARMOR_MODIFIER_TYPE
import at.orchaldir.gm.core.model.rpg.combat.ArmorModifierId
import at.orchaldir.gm.core.model.util.SortArmorModifier
import at.orchaldir.gm.core.selector.item.getArmors
import at.orchaldir.gm.core.selector.util.sortArmorModifiers
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$ARMOR_MODIFIER_TYPE")
class ArmorModifierRoutes : Routes<ArmorModifierId, SortArmorModifier> {
    @Resource("all")
    class All(
        val sort: SortArmorModifier = SortArmorModifier.Name,
        val parent: ArmorModifierRoutes = ArmorModifierRoutes(),
    )

    @Resource("details")
    class Details(val id: ArmorModifierId, val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    @Resource("new")
    class New(val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    @Resource("delete")
    class Delete(val id: ArmorModifierId, val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    @Resource("edit")
    class Edit(val id: ArmorModifierId, val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    @Resource("preview")
    class Preview(val id: ArmorModifierId, val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    @Resource("update")
    class Update(val id: ArmorModifierId, val parent: ArmorModifierRoutes = ArmorModifierRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortArmorModifier) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ArmorModifierId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ArmorModifierId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: ArmorModifierId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ArmorModifierId) = call.application.href(Update(id))
}

fun Application.configureArmorModifierRouting() {
    routing {
        get<ArmorModifierRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ArmorModifierRoutes(),
                state.sortArmorModifiers(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Armors") { state.getArmors(it.id) },
                ),
            )
        }
        get<ArmorModifierRoutes.Details> { details ->
            handleShowElement(details.id, ArmorModifierRoutes(), HtmlBlockTag::showArmorModifier)
        }
        get<ArmorModifierRoutes.New> {
            handleCreateElement(ArmorModifierRoutes(), STORE.getState().getArmorModifierStorage())
        }
        get<ArmorModifierRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ArmorModifierRoutes())
        }
        get<ArmorModifierRoutes.Edit> { edit ->
            handleEditElement(edit.id, ArmorModifierRoutes(), HtmlBlockTag::editArmorModifier)
        }
        post<ArmorModifierRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                ArmorModifierRoutes(),
                ::parseArmorModifier,
                HtmlBlockTag::editArmorModifier
            )
        }
        post<ArmorModifierRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArmorModifier)
        }
    }
}
