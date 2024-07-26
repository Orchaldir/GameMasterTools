package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.selectOneOrNone
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.html.svg
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.generator.EquipmentGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.selector.getEquipment2
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.visualization.character.visualizeCharacter
import io.ktor.http.*
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

fun Application.configureEquipmentRouting() {
    routing {
        get<Characters.Equipment.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)
            val equipment = state.getEquipment2(character)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, character, equipment)
            }
        }
        post<Characters.Equipment.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(preview.id)
            val formParameters = call.receiveParameters()
            val equipment = state.getEquipment2(parseEquipmentMap(formParameters))

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, character, equipment)
            }
        }
        post<Characters.Equipment.Update> { update ->
            logger.info { "Update character ${update.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(update.id)
            val formParameters = call.receiveParameters()
            val equipment = parseEquipmentMap(formParameters)

            //STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<Characters.Equipment.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(update.id)
            val generator = EquipmentGenerator.create(state, character)
            val equipment = generateEquipment(generator, character)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, character, equipment)
            }
        }
    }
}

private fun HTML.showEquipmentEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
    equipped: List<Equipment>,
) {
    val equipmentMap = character.equipmentMap
    val culture = state.cultures.getOrThrow(character.culture)
    val fashion = state.fashion.getOrThrow(culture.getFashion(character))
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Equipment.Preview(character.id))
    val updateLink = call.application.href(Characters.Equipment.Update(character.id))
    val generateLink = call.application.href(Characters.Equipment.Generate(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, character.appearance, equipped)
    val backSvg = visualizeCharacter(RENDER_CONFIG, character.appearance, equipped, false)

    simpleHtml("Edit Equipment: ${state.getName(character)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            p {
                submitInput {
                    value = "Random"
                    formAction = generateLink
                    formMethod = InputFormMethod.post
                }
            }

            selectEquipment(state, "Dresses", DRESS, equipmentMap, fashion.dresses, EquipmentType.Dress)
            selectEquipment(state, "Footwear", FOOTWEAR, equipmentMap, fashion.footwear, EquipmentType.Footwear)
            selectEquipment(state, "Hats", HAT, equipmentMap, fashion.hats, EquipmentType.Hat)
            selectEquipment(state, "Pants", PANTS, equipmentMap, fashion.pants, EquipmentType.Pants)
            selectEquipment(state, "Shirts", SHIRT, equipmentMap, fashion.shirts, EquipmentType.Shirt)
            selectEquipment(state, "Skirts", SKIRT, equipmentMap, fashion.skirts, EquipmentType.Skirt)

            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.selectEquipment(
    state: State,
    typeLabel: String,
    param: String,
    equipmentMap: EquipmentMap,
    oneOf: OneOf<ItemTemplateId>,
    type: EquipmentType,
) {
    if (oneOf.isEmpty()) {
        return
    }

    selectOneOrNone(
        typeLabel, param, oneOf, equipmentMap.contains(type), true
    ) { id ->
        val itemTemplate = state.itemTemplates.getOrThrow(id)
        label = itemTemplate.name
        value = id.value.toString()
        selected = equipmentMap.contains(id)
    }
}
