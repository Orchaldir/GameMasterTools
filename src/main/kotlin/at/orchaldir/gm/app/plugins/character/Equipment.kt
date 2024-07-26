package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.selector.getEquipment
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
            val equipment = state.getEquipment(character.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, character, equipment)
            }
        }
        post<Characters.Equipment.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(preview.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val equipment = parseEquipment(formParameters, config, character)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, character, equipment)
            }
        }
        post<Characters.Equipment.Update> { update ->
            logger.info { "Update character ${update.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(update.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val equipment = parseEquipment(formParameters, config, character)

            //STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<Characters.Equipment.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s equipment" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(update.id)
            val config = createGenerationConfig(state, character)
            val equipment = generateEquipment(config, character)

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
    val appearance = character.appearance
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Equipment.Preview(character.id))
    val updateLink = call.application.href(Characters.Equipment.Update(character.id))
    val generateLink = call.application.href(Characters.Equipment.Generate(character.id))
    val frontSvg = visualizeCharacter(RENDER_CONFIG, appearance, equipped)
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
