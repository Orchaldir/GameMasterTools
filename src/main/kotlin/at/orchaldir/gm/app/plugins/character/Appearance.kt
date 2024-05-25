package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
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
private const val EYES_TYPE = "eyes"
private const val NO_EYES = "no"
private const val ONE_EYE = "one"
private const val TOW_EYES = "two"
private const val EYE_SIZE = "eye_size"
private const val EYE_SHAPE = "eye_shape"
private const val PUPIL_SHAPE = "pupil_shape"
private const val PUPIL_COLOR = "pupil_color"
private const val SCLERA_COLOR = "sclera_color"

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
                showSkinEditor(appearance.skin)
                selectEnum("Ear Type", EAR_TYPE, EarType.entries) { type ->
                    label = type.name
                    value = type.toString()
                    selected = appearance.head.earType == type
                }
                showEyesEditor(appearance.head.eyes)
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
    skin: Skin,
) {
    field("Skin") {
        select {
            id = SKIN_TYPE
            name = SKIN_TYPE
            onChange = ON_CHANGE_SCRIPT
            option {
                label = "Scales"
                value = SCALES
                selected = skin is Scales
            }
            option {
                label = "Normal Skin"
                value = NORMAL
                selected = skin is NormalSkin
            }
            option {
                label = "Exotic Skin"
                value = EXOTIC
                selected = skin is ExoticSkin
            }
        }
    }
    when (skin) {
        is Scales -> {
            selectEnum("Scale Color", EXOTIC_COLOR, Color.entries) { c ->
                label = c.name
                value = c.toString()
                selected = skin.color == c
            }
        }

        is ExoticSkin -> {
            selectEnum("Skin Color", EXOTIC_COLOR, Color.entries) { c ->
                label = c.name
                value = c.toString()
                selected = skin.color == c
            }
        }

        is NormalSkin -> {
            selectEnum("Skin Color", SKIN_COLOR, SkinColor.entries) { c ->
                label = c.name
                value = c.toString()
                selected = skin.color == c
            }
        }
    }
}

private fun FORM.showEyesEditor(
    eyes: Eyes,
) {
    field("Eyes") {
        select {
            id = EYES_TYPE
            name = EYES_TYPE
            onChange = ON_CHANGE_SCRIPT
            option {
                label = "No Eyes"
                value = NO_EYES
                selected = eyes is NoEyes
            }
            option {
                label = "One Eye"
                value = ONE_EYE
                selected = eyes is OneEye
            }
            option {
                label = "Two Eyes"
                value = TOW_EYES
                selected = eyes is TwoEyes
            }
        }
    }
    when (eyes) {
        is OneEye -> {
            showEyeEditor(eyes.eye)
            selectEnum("Eye Size", EYE_SIZE, Size.entries) { c ->
                label = c.name
                value = c.toString()
                selected = eyes.size == c
            }
        }

        is TwoEyes -> {
            showEyeEditor(eyes.eye)
        }

        else -> doNothing()
    }
}

private fun FORM.showEyeEditor(
    eye: Eye,
) {
    selectEnum("Eye Shape", EYE_SHAPE, EyeShape.entries) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.eyeShape == shape
    }
    selectEnum("Pupil Shape", PUPIL_SHAPE, PupilShape.entries) { shape ->
        label = shape.name
        value = shape.toString()
        selected = eye.pupilShape == shape
    }
    selectEnum("Pupil Color", PUPIL_COLOR, Color.entries) { color ->
        label = color.name
        value = color.toString()
        selected = eye.pupilColor == color
    }
    selectEnum("Sclera Color", SCLERA_COLOR, Color.entries) { color ->
        label = color.name
        value = color.toString()
        selected = eye.scleraColor == color
    }
}

private fun parseAppearance(parameters: Parameters): Appearance {
    return when (parameters[TYPE]) {
        HEAD -> {
            val earType = parse(parameters, EAR_TYPE, EarType.Round)
            val eyes = parseEyes(parameters)
            val head = Head(earType, eyes, NoMouth)
            val skin = parseSkin(parameters)
            return HeadOnly(head, skin)
        }

        else -> UndefinedAppearance
    }
}

private fun parseEyes(parameters: Parameters): Eyes {
    return when (parameters[EYES_TYPE]) {
        ONE_EYE -> {
            val eye = parseEye(parameters)
            val size = parse(parameters, EYE_SIZE, Size.Medium)
            return OneEye(eye, size)
        }

        TOW_EYES -> {
            val eye = parseEye(parameters)
            return TwoEyes(eye)
        }

        else -> NoEyes
    }
}

private fun parseEye(parameters: Parameters) = Eye(
    parse(parameters, EYE_SHAPE, EyeShape.Ellipse),
    parse(parameters, PUPIL_SHAPE, PupilShape.Circle),
    parse(parameters, PUPIL_COLOR, Color.Green),
    parse(parameters, SCLERA_COLOR, Color.White),
)

private fun parseSkin(parameters: Parameters): Skin {
    return when (parameters[SKIN_TYPE]) {
        SCALES -> {
            return Scales(parseExoticColor(parameters))
        }

        EXOTIC -> {
            return ExoticSkin(parseExoticColor(parameters))
        }

        NORMAL -> {
            val color = parse(parameters, SKIN_COLOR, SkinColor.Medium)
            return NormalSkin(color)
        }

        else -> NormalSkin(SkinColor.Medium)
    }
}

private fun parseExoticColor(parameters: Parameters) =
    parse(parameters, EXOTIC_COLOR, Color.Red)

private inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String, default: T): T =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) } ?: default