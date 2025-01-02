package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseMaterial
import at.orchaldir.gm.core.action.CreateMaterial
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialCategory
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.item.getItemTemplatesMadeOf
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

@Resource("/materials")
class MaterialRoutes {
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
        get<MaterialRoutes> {
            logger.info { "Get all materials" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMaterials(call, STORE.getState())
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

            call.respondRedirect(call.application.href(MaterialRoutes()))

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
) {
    val materials = STORE.getState().getMaterialStorage().getAll().sortedBy { it.name }
    val count = materials.size
    val createLink = call.application.href(MaterialRoutes.New())

    simpleHtml("Materials") {
        field("Count", count.toString())
        table {
            tr {
                th { +"Name" }
                th { +"Category" }
                th { +"Items" }
                th { +"Streets" }
            }
            materials.forEach { material ->
                tr {
                    td { link(call, state, material) }
                    td { +material.category.toString() }
                    tdSkipZero(state.getItemTemplatesMadeOf(material.id).count())
                    tdSkipZero(state.getStreetTemplatesMadeOf(material.id).count())
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
    val itemTemplates = state.getItemTemplatesMadeOf(material.id)
    val streetTemplates = state.getStreetTemplatesMadeOf(material.id)
    val backLink = call.application.href(MaterialRoutes())
    val deleteLink = call.application.href(MaterialRoutes.Delete(material.id))
    val editLink = call.application.href(MaterialRoutes.Edit(material.id))

    simpleHtml("Material: ${material.name}") {
        field("Name", material.name)
        field("Category", material.category.toString())
        showList("Item Templates", itemTemplates) { template ->
            link(call, template)
        }
        showList("Street Templates", streetTemplates) { template ->
            link(call, template)
        }
        action(editLink, "Edit")
        if (state.canDelete(material.id)) {
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

    simpleHtml("Edit Material: ${material.name}") {
        form {
            selectName(material.name)
            selectValue("Category", CATEGORY, MaterialCategory.entries) { category ->
                label = category.name
                value = category.name
                selected = material.category == category
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}
