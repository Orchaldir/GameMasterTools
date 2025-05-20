package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.APPEARANCE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.editAppearance
import at.orchaldir.gm.app.html.character.appearance.parseAppearance
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.selector.culture.getFashion
import at.orchaldir.gm.core.selector.race.getRaceAppearance
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
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
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

fun Application.configureAppearanceRouting() {
    routing {
        get<CharacterRoutes.Appearance.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Appearance.Preview> { preview ->
            logger.info { "Get preview for character ${preview.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(preview.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val appearance = parseAppearance(formParameters, config, character)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, updatedCharacter)
            }
        }
        post<CharacterRoutes.Appearance.Update> { update ->
            logger.info { "Update character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(update.id)
            val formParameters = call.receiveParameters()
            val config = createGenerationConfig(state, character)
            val appearance = parseAppearance(formParameters, config, character)

            STORE.dispatch(UpdateAppearance(update.id, appearance))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        post<CharacterRoutes.Appearance.Generate> { update ->
            logger.info { "Generate character ${update.id.value}'s appearance" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(update.id)
            val config = createGenerationConfig(state, character)
            val appearance = generateAppearance(config, character)
            val updatedCharacter = character.copy(appearance = appearance)

            call.respondHtml(HttpStatusCode.OK) {
                showAppearanceEditor(call, state, updatedCharacter)
            }
        }
    }
}

private fun HTML.showAppearanceEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val appearance = character.appearance
    val raceAppearance = state.getRaceAppearance(character)
    val style = state.getFashion(character)?.appearance
    val backLink = href(call, character.id)
    val previewLink = call.application.href(CharacterRoutes.Appearance.Preview(character.id))
    val updateLink = call.application.href(CharacterRoutes.Appearance.Update(character.id))
    val generateLink = call.application.href(CharacterRoutes.Appearance.Generate(character.id))
    val frontSvg = visualizeCharacter(CHARACTER_CONFIG, state, character)
    val backSvg = visualizeCharacter(CHARACTER_CONFIG, state, character, renderFront = false)

    simpleHtml("Edit Appearance: ${character.name(state)}", true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                button("Random", generateLink)

                editAppearance(state, raceAppearance, appearance, character, style)
            }
        }, {
            svg(frontSvg, 80)
            svg(backSvg, 80)
        })
    }
}

fun createGenerationConfig(state: State, character: Character): AppearanceGeneratorConfig {
    val fashion = state.getFashion(character)
    val race = state.getRaceStorage().getOrThrow(character.race)

    return AppearanceGeneratorConfig(
        RandomNumberGenerator(Random),
        state.rarityGenerator,
        character.gender,
        race.height,
        state.getRaceAppearance(character),
        fashion?.appearance ?: AppearanceFashion(),
    )
}

fun generateAppearance(
    config: AppearanceGeneratorConfig,
    character: Character,
): Appearance {
    val type = config.generate(config.appearanceOptions.appearanceTypes)
    val parameters = parametersOf(APPEARANCE, type.toString())

    return parseAppearance(parameters, config, character)
}
