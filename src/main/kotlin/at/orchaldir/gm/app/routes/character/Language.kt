package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.AddLanguage
import at.orchaldir.gm.core.action.RemoveLanguages
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.core.selector.getName
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val REMOVE = "remove"

fun Application.configureCharacterLanguageRouting() {
    routing {
        get<CharacterRoutes.Languages.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s languages" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, character)
            }
        }
        post<CharacterRoutes.Languages.Update> { update ->
            logger.info { "Update character ${update.id.value}'s languages" }

            val formParameters = call.receiveParameters()
            val languageParam = formParameters["language"]

            if (!languageParam.isNullOrEmpty()) {
                val language = LanguageId(languageParam.toInt())
                val level = ComprehensionLevel.valueOf(formParameters.getOrFail("level"))

                STORE.dispatch(AddLanguage(update.id, language, level))
            }

            val removeList = formParameters.getAll(REMOVE)?.map { LanguageId(it.toInt()) }

            if (removeList != null) {
                STORE.dispatch(RemoveLanguages(update.id, removeList.toSet()))
            }

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val cultures = state.getCultureStorage().getOrThrow(character.culture)
    val backLink = href(call, character.id)
    val updateLink = call.application.href(CharacterRoutes.Languages.Update(character.id))

    simpleHtml("Edit Languages: ${state.getName(character)}") {
        showLanguages(call, state, character)
        form {
            field("Language to Update") {
                select {
                    id = "language"
                    name = "language"
                    option {
                        label = ""
                        value = ""
                        selected = true
                    }
                    reverseAndSort(cultures.languages.getRarityMap())
                        .forEach { (rarity, values) ->
                            optGroup(rarity.toString()) {
                                values.forEach { languageId ->
                                    val language = state.getLanguageStorage().getOrThrow(languageId)
                                    option {
                                        label = language.name
                                        value = language.id.value.toString()
                                    }
                                }
                            }
                        }
                }
            }
            selectValue("Comprehension Level", "level", ComprehensionLevel.entries) { level ->
                label = level.toString()
                value = level.toString()
                selected = level == ComprehensionLevel.Native
            }
            field("Languages to Remove") {
                character.languages.keys.forEach { id ->
                    val language = state.getLanguageStorage().getOrThrow(id)
                    p {
                        checkBoxInput {
                            name = REMOVE
                            value = language.id.value.toString()
                            +language.name
                        }
                    }
                }
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}