package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.editEquipmentMap
import at.orchaldir.gm.app.html.character.parseEquipmentMap
import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.generator.EquipmentGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.selector.item.getEquipment
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
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureEquipmentMapRouting() {
    routing {
        get<CharacterRoutes.Equipment.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentMapEditor(call, state, character, character.equipmentMap)
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
                showEquipmentMapEditor(call, state, character, equipmentMap)
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
                    showEquipmentMapEditor(call, state, generator.character, equipment)
                }
            } else {
                call.respondRedirect(href(call, update.id))
            }
        }
    }
}

private fun HTML.showEquipmentMapEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
    equipmentMap: EquipmentMap<EquipmentId>,
) {
    val generator = EquipmentGenerator.create(state, character.id)
    val equipped = state.getEquipment(equipmentMap)
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Equipment.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Equipment.Update(character.id))
    val generateLink = call.application.href(CharacterRoutes.Equipment.Generate(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipped)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, equipped, false)

    simpleHtml("Edit Equipment of ${character.name(state)}") {
        svg(frontSvg, 20)
        svg(backSvg, 20)
        formWithPreview(previewLink, updateLink, backLink) {
            button("Random", generateLink, generator == null)

            editEquipmentMap(state, equipmentMap)
        }
    }
}
