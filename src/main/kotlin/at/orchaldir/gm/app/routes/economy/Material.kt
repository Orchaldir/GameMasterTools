package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.editMaterial
import at.orchaldir.gm.app.html.economy.material.parseMaterial
import at.orchaldir.gm.app.html.economy.material.showMaterial
import at.orchaldir.gm.core.action.CreateMaterial
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MATERIAL_TYPE
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.SortMaterial
import at.orchaldir.gm.core.selector.economy.canDeleteMaterial
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.item.getTextsMadeOf
import at.orchaldir.gm.core.selector.race.countRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.race.getRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import at.orchaldir.gm.core.selector.util.sortEquipmentList
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.core.selector.world.countStreetTemplates
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$MATERIAL_TYPE")
class MaterialRoutes {
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
            logger.info { "Get details of material ${details.id.value}" }

            val state = STORE.getState()
            val material = state.getMaterialStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMaterialDetails(call, state, material)
            }
        }
        get<MaterialRoutes.New> {
            logger.info { "Add new material" }

            STORE.dispatch(CreateMaterial)

            call.respondRedirect(
                call.application.href(
                    MaterialRoutes.Edit(
                        STORE.getState().getMaterialStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<MaterialRoutes.Delete> { delete ->
            logger.info { "Delete material ${delete.id.value}" }

            STORE.dispatch(DeleteMaterial(delete.id))

            call.respondRedirect(call.application.href(MaterialRoutes.All()))

            STORE.getState().save()
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
            logger.info { "Update material ${update.id.value}" }

            val material = parseMaterial(update.id, call.receiveParameters())

            STORE.dispatch(UpdateMaterial(material))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
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

private fun HTML.showMaterialDetails(
    call: ApplicationCall,
    state: State,
    material: Material,
) {
    val currencyUnits = state.sortCurrencyUnits(state.getCurrencyUnits(material.id))
    val equipmentList = state.sortEquipmentList(state.getEquipmentMadeOf(material.id))
    val moons = state.getMoonsContaining(material.id)
    val regions = state.getRegionsContaining(material.id)
    val raceAppearances = state.getRaceAppearancesMadeOf(material.id)
    val streetTemplates = state.getStreetTemplatesMadeOf(material.id)
    val texts = state.getTextsMadeOf(material.id)
    val backLink = call.application.href(MaterialRoutes.All())
    val deleteLink = call.application.href(MaterialRoutes.Delete(material.id))
    val editLink = call.application.href(MaterialRoutes.Edit(material.id))

    simpleHtmlDetails(material) {
        showMaterial(material)

        h2 { +"Usage" }

        fieldList(call, state, currencyUnits)
        fieldList(call, state, equipmentList)
        fieldList(call, state, moons)
        fieldList(call, state, regions)
        fieldList(call, state, raceAppearances)
        fieldList(call, state, streetTemplates)
        fieldList("Texts", texts) { text ->
            link(call, text, text.getNameWithDate(state))
        }

        action(editLink, "Edit")
        if (state.canDeleteMaterial(material.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
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
