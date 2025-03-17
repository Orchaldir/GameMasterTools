package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.equipment.editEquipment
import at.orchaldir.gm.app.html.model.item.equipment.parseEquipment
import at.orchaldir.gm.app.html.model.item.equipment.showEquipment
import at.orchaldir.gm.core.action.CreateEquipment
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EQUIPMENT_TYPE
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.selector.getFashions
import at.orchaldir.gm.core.selector.item.canDelete
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
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

@Resource("/$EQUIPMENT_TYPE")
class EquipmentRoutes {
    @Resource("details")
    class Details(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("new")
    class New(val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("delete")
    class Delete(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("edit")
    class Edit(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("preview")
    class Preview(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("update")
    class Update(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())
}

fun Application.configureEquipmentRouting() {
    routing {
        get<EquipmentRoutes> {
            logger.info { "Get all equipments" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllEquipment(call, STORE.getState())
            }
        }
        get<EquipmentRoutes.Details> { details ->
            logger.info { "Get details of equipment ${details.id.value}" }

            val state = STORE.getState()
            val equipment = state.getEquipmentStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentDetails(call, state, equipment)
            }
        }
        get<EquipmentRoutes.New> {
            logger.info { "Add new equipment" }

            STORE.dispatch(CreateEquipment)

            call.respondRedirect(
                call.application.href(
                    EquipmentRoutes.Edit(
                        STORE.getState().getEquipmentStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<EquipmentRoutes.Delete> { delete ->
            logger.info { "Delete equipment ${delete.id.value}" }

            STORE.dispatch(DeleteEquipment(delete.id))

            call.respondRedirect(call.application.href(EquipmentRoutes()))

            STORE.getState().save()
        }
        get<EquipmentRoutes.Edit> { edit ->
            logger.info { "Get editor for equipment ${edit.id.value}" }

            val state = STORE.getState()
            val template = state.getEquipmentStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, template)
            }
        }
        post<EquipmentRoutes.Preview> { preview ->
            logger.info { "Get preview for equipment ${preview.id.value}" }

            val equipment = parseEquipment(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, STORE.getState(), equipment)
            }
        }
        post<EquipmentRoutes.Update> { update ->
            logger.info { "Update equipment ${update.id.value}" }

            val equipment = parseEquipment(update.id, call.receiveParameters())

            STORE.dispatch(UpdateEquipment(equipment))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllEquipment(
    call: ApplicationCall,
    state: State,
) {
    val equipmentList = state.getEquipmentStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(EquipmentRoutes.New())

    simpleHtml("Equipment") {
        field("Count", equipmentList.size)

        table {
            tr {
                th { +"Name" }
                th { +"Type" }
                th { +"Weight" }
                th { +"Characters" }
                th { +"Fashion" }
            }
            equipmentList.forEach { equipment ->
                tr {
                    td { link(call, equipment) }
                    tdEnum(equipment.data.getType())
                    td { +equipment.weight.toString() }
                    tdSkipZero(state.getEquippedBy(equipment.id).size)
                    tdSkipZero(state.getFashions(equipment.id).size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showEquipmentDetails(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    val characters = state.getEquippedBy(equipment.id)
    val fashions = state.getFashions(equipment.id)
    val backLink = call.application.href(EquipmentRoutes())
    val deleteLink = call.application.href(EquipmentRoutes.Delete(equipment.id))
    val editLink = call.application.href(EquipmentRoutes.Edit(equipment.id))

    simpleHtml("Equipment: ${equipment.name}") {
        visualizeItem(equipment)

        showEquipment(call, state, equipment)

        showList("Equipped By", characters) { item ->
            link(call, state, item)
        }
        showList("Part of Fashion", fashions) { item ->
            link(call, item)
        }

        action(editLink, "Edit")

        if (state.canDelete(equipment.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showEquipmentEditor(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    val backLink = href(call, equipment.id)
    val previewLink = call.application.href(EquipmentRoutes.Preview(equipment.id))
    val updateLink = call.application.href(EquipmentRoutes.Update(equipment.id))

    simpleHtml("Edit equipment: ${equipment.name}") {
        visualizeItem(equipment)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post

            editEquipment(state, equipment)

            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun BODY.visualizeItem(template: Equipment) {
    if (template.data.getType() != EquipmentDataType.None) {
        val equipped = listOf(template.data)
        val appearance = HumanoidBody(Body(), Head(), Distance.fromMeters(1.0f))
        val frontSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped)
        val backSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped, false)
        svg(frontSvg, 20)
        svg(backSvg, 20)
    }
}