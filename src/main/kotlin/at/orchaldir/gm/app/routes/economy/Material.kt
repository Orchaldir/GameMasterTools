package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.editMaterial
import at.orchaldir.gm.app.html.economy.material.parseMaterial
import at.orchaldir.gm.app.html.economy.material.showMaterial
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.SortMaterial
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.race.countRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.core.selector.world.countStreetTemplates
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$MATERIAL_TYPE")
class MaterialRoutes : Routes<MaterialId> {
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

    @Resource("update")
    class Update(val id: MaterialId, val parent: MaterialRoutes = MaterialRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: MaterialId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MaterialId) = call.application.href(Edit(id))
}

fun Application.configureMaterialRouting() {
    routing {
        get<MaterialRoutes.All> { all ->
            logger.info { "Get all texts" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMaterials(call, STORE.getState(), all.sort)
            }
        }
        get<MaterialRoutes.Details> { details ->
            handleShowElement(details.id, MaterialRoutes(), HtmlBlockTag::showMaterial)
        }
        get<MaterialRoutes.New> {
            handleCreateElement(STORE.getState().getMaterialStorage()) { id ->
                MaterialRoutes.Edit(id)
            }
        }
        get<MaterialRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MaterialRoutes.All())
        }
        get<MaterialRoutes.Edit> { edit ->
            logger.info { "Get editor for material ${edit.id.value}" }

            val state = STORE.getState()
            val material = state.getMaterialStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMaterialEditor(call, material)
            }
        }
        post<MaterialRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMaterial)
        }
    }
}

private fun HTML.showAllMaterials(
    call: ApplicationCall,
    state: State,
    sort: SortMaterial,
) {
    val materials = state.sortMaterial(sort)
    val createLink = call.application.href(MaterialRoutes.New())

    simpleHtml("Materials") {
        field("Count", materials.size)
        showSortTableLinks(call, SortMaterial.entries, MaterialRoutes(), MaterialRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Category" }
                th { +"Color" }
                th { +"Density" }
                th { +"Currency" }
                th { +"Equipment" }
                th { +"Race App" }
                th { +"Streets" }
                th { +"Texts" }
            }
            materials.forEach { material ->
                tr {
                    tdLink(call, state, material)
                    tdEnum(material.category)
                    tdColor(material.color)
                    td(material.density)
                    tdSkipZero(state.countCurrencyUnits(material.id))
                    tdSkipZero(state.countEquipment(material.id))
                    tdSkipZero(state.countRaceAppearancesMadeOf(material.id))
                    tdSkipZero(state.countStreetTemplates(material.id))
                    tdSkipZero(state.countTexts(material.id))
                }
            }
        }
        showMaterialCategoryCount(materials)
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showMaterialEditor(
    call: ApplicationCall,
    material: Material,
) {
    val backLink = href(call, material.id)
    val updateLink = call.application.href(MaterialRoutes.Update(material.id))

    simpleHtmlEditor(material) {
        form {
            editMaterial(material)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
