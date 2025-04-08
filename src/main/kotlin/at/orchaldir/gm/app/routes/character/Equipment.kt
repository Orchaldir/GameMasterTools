package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseEquipmentMap
import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.generator.EquipmentGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.selector.item.getEquipment
import at.orchaldir.gm.core.selector.item.getEquipmentOf
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
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

fun Application.configureCharacterEquipmentRouting() {
    routing {
        get<CharacterRoutes.Equipment.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEquipmentEditor(call, state, character, character.equipmentMap)
            }
        }
        post<CharacterRoutes.Equipment.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(preview.id)
            val formParameters = call.receiveParameters()
            val equipmentMap = parseEquipmentMap(formParameters)

            logger.info { "equipment: $equipmentMap" }

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEquipmentEditor(call, state, character, equipmentMap)
            }
        }
        post<CharacterRoutes.Equipment.Update> { update ->
            logger.info { "Update character ${update.id.value}'s equipment" }

            val formParameters = call.receiveParameters()
            val equipmentMap = parseEquipmentMap(formParameters)

            STORE.dispatch(UpdateEquipmentOfCharacter(update.id, equipmentMap))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<CharacterRoutes.Equipment.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s equipment" }

            val state = STORE.getState()
            val generator = EquipmentGenerator.create(state, update.id)

            if (generator != null) {
                val equipment = generator.generate()

                call.respondHtml(HttpStatusCode.OK) {
                    showCharacterEquipmentEditor(call, state, generator.character, equipment)
                }
            } else {
                call.respondRedirect(href(call, update.id))
            }
        }
    }
}

private fun HTML.showCharacterEquipmentEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
    equipmentMap: EquipmentMap,
) {
    val equipped = state.getEquipment(equipmentMap)
    val occupiedSlots = equipmentMap.getOccupiedSlots()
    val culture = state.getCultureStorage().getOrThrow(character.culture)
    val fashion = state.getFashionStorage().getOptional(culture.getFashion(character))
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Equipment.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Equipment.Update(character.id))
    val generateLink = call.application.href(CharacterRoutes.Equipment.Generate(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipped)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipped, false)

    simpleHtml("Edit Equipment of ${character.name(state)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            button("Random", generateLink)

            EquipmentDataType.entries.forEach { selectEquipment(state, equipmentMap, occupiedSlots, fashion, it) }

            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.selectEquipment(
    state: State,
    equipmentMap: EquipmentMap,
    occupiedSlots: Set<EquipmentSlot>,
    fashion: Fashion?,
    type: EquipmentDataType,
) {
    // ignore fashion for testing
    val options = OneOrNone(state.getEquipmentOf(type).map { it.id })

    if (options.isEmpty()) {
        return
    }

    val isTypeEquipped = equipmentMap.contains(type)
    val canSelect = isTypeEquipped || type.slots().none { occupiedSlots.contains(it) }

    selectOneOrNone(
        type.name, type.name, options, !isTypeEquipped, true
    ) { id ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        label = equipment.name
        value = id.value.toString()
        selected = equipmentMap.contains(id)
        disabled = !canSelect
    }
}
