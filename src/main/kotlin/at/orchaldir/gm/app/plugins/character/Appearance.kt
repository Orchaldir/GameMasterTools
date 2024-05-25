package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureAppearanceRouting() {
    routing {
        get<Characters.Appearance.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, character)
            }
        }
    }
}

private fun HTML.showAppearanceEditor(
    call: ApplicationCall,
    character: Character,
) {
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Appearance.Preview(character.id))
    val updateLink = call.application.href(Characters.Appearance.Update(character.id))

    simpleHtml("Edit Appearance: ${character.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Skin") {
                select {
                    id = "skin"
                    name = "skin"
                    onChange = ON_CHANGE_SCRIPT
                    option {
                        label = "Scales"
                        value = "Scales"
                        selected = character.appearance?.skin is Scales
                    }
                    option {
                        label = "Normal Skin"
                        value = "Normal"
                        selected = character.appearance?.skin is NormalSkin
                    }
                    option {
                        label = "Exotic Skin"
                        value = "Exotic"
                        selected = character.appearance?.skin is ExoticSkin
                    }
                }
            }
            when (character.appearance?.skin) {
                is Scales -> {
                    selectEnum("Scale Color", "skin_color", Color.entries) { c ->
                        label = c.name
                        value = c.toString()
                        selected = character.appearance?.skin.color == c
                    }
                }

                is ExoticSkin -> {
                    selectEnum("Skin Color", "skin_color", Color.entries) { c ->
                        label = c.name
                        value = c.toString()
                        selected = character.appearance?.skin.color == c
                    }
                }

                is NormalSkin -> {
                    selectEnum("Skin Color", "skin_color", SkinColor.entries) { c ->
                        label = c.name
                        value = c.toString()
                        selected = character.appearance?.skin.color == c
                    }
                }

                else -> doNothing()
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