package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.combat.displayProtection
import at.orchaldir.gm.app.html.rpg.combat.editArmorType
import at.orchaldir.gm.app.html.rpg.combat.parseArmorType
import at.orchaldir.gm.app.html.rpg.combat.showArmorType
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.combat.ARMOR_TYPE_TYPE
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import at.orchaldir.gm.core.model.util.SortArmorType
import at.orchaldir.gm.core.selector.item.getArmors
import at.orchaldir.gm.core.selector.util.sortArmorTypes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$ARMOR_TYPE_TYPE")
class ArmorTypeRoutes : Routes<ArmorTypeId, SortArmorType> {
    @Resource("all")
    class All(
        val sort: SortArmorType = SortArmorType.Name,
        val parent: ArmorTypeRoutes = ArmorTypeRoutes(),
    )

    @Resource("details")
    class Details(val id: ArmorTypeId, val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    @Resource("new")
    class New(val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    @Resource("delete")
    class Delete(val id: ArmorTypeId, val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    @Resource("edit")
    class Edit(val id: ArmorTypeId, val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    @Resource("preview")
    class Preview(val id: ArmorTypeId, val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    @Resource("update")
    class Update(val id: ArmorTypeId, val parent: ArmorTypeRoutes = ArmorTypeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortArmorType) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ArmorTypeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ArmorTypeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: ArmorTypeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ArmorTypeId) = call.application.href(Update(id))
}

fun Application.configureArmorTypeRouting() {
    routing {
        get<ArmorTypeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ArmorTypeRoutes(),
                state.sortArmorTypes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Protection") { displayProtection(call, state, it.protection) },
                    countCollectionColumn("Armor") { state.getArmors(it.id) },
                ),
            )
        }
        get<ArmorTypeRoutes.Details> { details ->
            handleShowElement(details.id, ArmorTypeRoutes(), HtmlBlockTag::showArmorType)
        }
        get<ArmorTypeRoutes.New> {
            handleCreateElement(ArmorTypeRoutes(), STORE.getState().getArmorTypeStorage())
        }
        get<ArmorTypeRoutes.Delete> { delete ->
            handleDeleteElement(ArmorTypeRoutes(), delete.id)
        }
        get<ArmorTypeRoutes.Edit> { edit ->
            handleEditElement(edit.id, ArmorTypeRoutes(), HtmlBlockTag::editArmorType)
        }
        post<ArmorTypeRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                ArmorTypeRoutes(),
                ::parseArmorType,
                HtmlBlockTag::editArmorType
            )
        }
        post<ArmorTypeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArmorType)
        }
    }
}
