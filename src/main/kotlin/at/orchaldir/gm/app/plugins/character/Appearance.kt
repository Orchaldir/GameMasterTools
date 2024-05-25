package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val SKIN_COLOR = "skin_color"

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
        post<Characters.Appearance.Preview> { edit ->
            logger.info { "Get preview for character ${edit.id.value}'s relationships" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)
            val formParameters = call.receiveParameters()
            val appearance = parseAppearance(formParameters)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, updatedCharacter)
            }
        }
    }
}

private fun HTML.showAppearanceEditor(
    call: ApplicationCall,
    character: Character,
) {
    val appearance = character.appearance
    val backLink = href(call, character.id)
    val previewLink = call.application.href(Characters.Appearance.Preview(character.id))
    val updateLink = call.application.href(Characters.Appearance.Update(character.id))

    simpleHtml("Edit Appearance: ${character.name}") {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Appearance Type") {
                select {
                    id = "type"
                    name = "type"
                    onChange = ON_CHANGE_SCRIPT
                    option {
                        label = "Undefined"
                        value = "Undefined"
                        selected = appearance is UndefinedAppearance
                    }
                    option {
                        label = "Head Only"
                        value = "Head"
                        selected = appearance is HeadOnly
                    }
                }
            }
            if (appearance is HeadOnly) {
                field("Skin") {
                    select {
                        id = "skin"
                        name = "skin"
                        onChange = ON_CHANGE_SCRIPT
                        option {
                            label = "Scales"
                            value = "Scales"
                            selected = appearance.skin is Scales
                        }
                        option {
                            label = "Normal Skin"
                            value = "Normal"
                            selected = appearance.skin is NormalSkin
                        }
                        option {
                            label = "Exotic Skin"
                            value = "Exotic"
                            selected = appearance.skin is ExoticSkin
                        }
                    }
                }
                when (appearance.skin) {
                    is Scales -> {
                        selectEnum("Scale Color", SKIN_COLOR, Color.entries) { c ->
                            label = c.name
                            value = c.toString()
                            selected = appearance.skin.color == c
                        }
                    }

                    is ExoticSkin -> {
                        selectEnum("Skin Color", SKIN_COLOR, Color.entries) { c ->
                            label = c.name
                            value = c.toString()
                            selected = appearance.skin.color == c
                        }
                    }

                    is NormalSkin -> {
                        selectEnum("Skin Color", SKIN_COLOR, SkinColor.entries) { c ->
                            label = c.name
                            value = c.toString()
                            selected = appearance.skin.color == c
                        }
                    }
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

private fun parseAppearance(parameters: Parameters): Appearance {
    return when (parameters["type"]) {
        "Head" -> {
            val head = Head(EarType.Round, NoEyes, NoMouth)
            val skin = parseSkin(parameters)
            return HeadOnly(head, skin)
        }

        else -> UndefinedAppearance
    }
}

private fun parseSkin(parameters: Parameters): Skin {
    return when (parameters["skin"]) {
        "Scales" -> {
            val color = parameters[SKIN_COLOR]?.let { Color.valueOf(it) } ?: Color.Red
            return Scales(color)
        }

        "Exotic" -> {
            val color = parameters[SKIN_COLOR]?.let { Color.valueOf(it) } ?: Color.Red
            return ExoticSkin(color)
        }

        "Normal" -> {
            val color = parameters[SKIN_COLOR]?.let { SkinColor.valueOf(it) } ?: SkinColor.Medium
            return NormalSkin(color)
        }

        else -> NormalSkin(SkinColor.Medium)
    }
}