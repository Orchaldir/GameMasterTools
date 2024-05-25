package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
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

private const val TYPE = "type"
private const val HEAD = "head"
private const val SKIN_TYPE = "skin"
private const val SCALES = "scales"
private const val NORMAL = "normal"
private const val EXOTIC = "exotic"
private const val EXOTIC_COLOR = "exotic_color"
private const val SKIN_COLOR = "skin_color"
private const val EAR_TYPE = "ear_type"

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
            logger.info { "Get preview for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)
            val formParameters = call.receiveParameters()
            val appearance = parseAppearance(formParameters)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, updatedCharacter)
            }
        }
        post<Characters.Appearance.Update> { update ->
            logger.info { "Update character ${update.id.value}'s appearance" }

            val formParameters = call.receiveParameters()
            val appearance = parseAppearance(formParameters)

            STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))
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
                    id = TYPE
                    name = TYPE
                    onChange = ON_CHANGE_SCRIPT
                    option {
                        label = "Undefined"
                        value = "Undefined"
                        selected = appearance is UndefinedAppearance
                    }
                    option {
                        label = "Head Only"
                        value = HEAD
                        selected = appearance is HeadOnly
                    }
                }
            }
            if (appearance is HeadOnly) {
                showSkinEditor(appearance)
                selectEnum("Ear Type", EAR_TYPE, EarType.entries) { type ->
                    label = type.name
                    value = type.toString()
                    selected = appearance.head.earType == type
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

private fun FORM.showSkinEditor(
    appearance: HeadOnly,
) {
    field("Skin") {
        select {
            id = SKIN_TYPE
            name = SKIN_TYPE
            onChange = ON_CHANGE_SCRIPT
            option {
                label = "Scales"
                value = SCALES
                selected = appearance.skin is Scales
            }
            option {
                label = "Normal Skin"
                value = NORMAL
                selected = appearance.skin is NormalSkin
            }
            option {
                label = "Exotic Skin"
                value = EXOTIC
                selected = appearance.skin is ExoticSkin
            }
        }
    }
    when (appearance.skin) {
        is Scales -> {
            selectEnum("Scale Color", EXOTIC_COLOR, Color.entries) { c ->
                label = c.name
                value = c.toString()
                selected = appearance.skin.color == c
            }
        }

        is ExoticSkin -> {
            selectEnum("Skin Color", EXOTIC_COLOR, Color.entries) { c ->
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

private fun parseAppearance(parameters: Parameters): Appearance {
    return when (parameters[TYPE]) {
        HEAD -> {
            val earType = parameters[EAR_TYPE]?.let { EarType.valueOf(it) } ?: EarType.Round
            val head = Head(earType, NoEyes, NoMouth)
            val skin = parseSkin(parameters)
            return HeadOnly(head, skin)
        }

        else -> UndefinedAppearance
    }
}

private fun parseSkin(parameters: Parameters): Skin {
    return when (parameters[SKIN_TYPE]) {
        SCALES -> {
            return Scales(parseExoticColor(parameters))
        }

        EXOTIC -> {
            return ExoticSkin(parseExoticColor(parameters))
        }

        NORMAL -> {
            val color = parameters[SKIN_COLOR]?.let { SkinColor.valueOf(it) } ?: SkinColor.Medium
            return NormalSkin(color)
        }

        else -> NormalSkin(SkinColor.Medium)
    }
}

private fun parseExoticColor(parameters: Parameters) =
    parameters[EXOTIC_COLOR]?.let { Color.valueOf(it) } ?: Color.Red