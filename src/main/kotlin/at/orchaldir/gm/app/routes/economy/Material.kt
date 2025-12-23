package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.economy.material.editMaterial
import at.orchaldir.gm.app.html.economy.material.parseMaterial
import at.orchaldir.gm.app.html.economy.material.showMaterial
import at.orchaldir.gm.app.html.economy.money.displayPrice
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.economy.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.SortMaterial
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.equipment.countEquipment
import at.orchaldir.gm.core.selector.race.countRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.core.selector.world.countStreetTemplates
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MATERIAL_TYPE")
class MaterialRoutes : Routes<MaterialId, SortMaterial> {
    @Resource("all")
    class All(
        val sort: SortMaterial = SortMaterial.Name,
        val parent: MaterialRoutes = MaterialRoutes(),
    )

    @Resource("details")
    class Details(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    @Resource("new")
    class New(val parent: MaterialRoutes = MaterialRoutes())

    @Resource("delete")
    class Delete(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    @Resource("edit")
    class Edit(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    @Resource("preview")
    class Preview(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    @Resource("update")
    class Update(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMaterial) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MaterialId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MaterialId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MaterialId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MaterialId) = call.application.href(Update(id))
}

fun Application.configureMaterialRouting() {
    routing {
        get<MaterialRoutes.All> { all ->
            val state = STORE.getState()
            val currency = state.getDefaultCurrency()

            handleShowAllElements(
                MaterialRoutes(),
                state.sortMaterials(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Category") { tdEnum(it.category) },
                    Column("Color") { tdColor(it.color) },
                    Column("Density") { td(it.density) },
                    tdColumn("Price") { displayPrice(call, currency, it.pricePerKilogram) },
                    countColumnForId("Currency", state::countCurrencyUnits),
                    countColumnForId("Equipment", state::countEquipment),
                    countColumnForId(listOf("Race", "Appearance"), state::countRaceAppearancesMadeOf),
                    countColumnForId("Streets", state::countStreetTemplates),
                    countColumnForId("Texts", state::countTexts),
                ),
            ) {
                showMaterialCategoryCount(it)
            }
        }
        get<MaterialRoutes.Details> { details ->
            handleShowElement(details.id, MaterialRoutes(), HtmlBlockTag::showMaterial)
        }
        get<MaterialRoutes.New> {
            handleCreateElement(MaterialRoutes(), STORE.getState().getMaterialStorage())
        }
        get<MaterialRoutes.Delete> { delete ->
            handleDeleteElement(MaterialRoutes(), delete.id)
        }
        get<MaterialRoutes.Edit> { edit ->
            handleEditElement(edit.id, MaterialRoutes(), HtmlBlockTag::editMaterial)
        }
        post<MaterialRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, MaterialRoutes(), ::parseMaterial, HtmlBlockTag::editMaterial)
        }
        post<MaterialRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMaterial)
        }
    }
}
