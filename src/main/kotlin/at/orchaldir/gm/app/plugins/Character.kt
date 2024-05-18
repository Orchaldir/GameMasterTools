package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.core.selector.getPossibleLanguages
import io.ktor.http.*
import io.ktor.resources.*
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

@Resource("/characters")
class Characters {

    @Resource("details")
    class Details(val id: CharacterId, val parent: Characters = Characters())

    @Resource("new")
    class New(val parent: Characters = Characters())

    @Resource("delete")
    class Delete(val id: CharacterId, val parent: Characters = Characters())

    @Resource("edit")
    class Edit(val id: CharacterId, val parent: Characters = Characters())

    @Resource("update")
    class Update(val id: CharacterId, val parent: Characters = Characters())

    @Resource("/languages")
    class Languages(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Languages = Languages())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Languages = Languages())
    }
}

fun Application.configureCharacterRouting() {
    routing {
        get<Characters> {
            logger.info { "Get all characters" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters(call)
            }
        }
        get<Characters.Details> { details ->
            logger.info { "Get details of character ${details.id.value}" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterDetails(call, state, character)
            }
        }
        get<Characters.New> {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondRedirect(call.application.href(Characters.Edit(STORE.getState().characters.lastId)))
        }
        get<Characters.Delete> { delete ->
            logger.info { "Delete character ${delete.id.value}" }

            STORE.dispatch(DeleteCharacter(delete.id))

            call.respondRedirect(call.application.href(Characters()))
        }
        get<Characters.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, state, character)
            }
        }
        post<Characters.Update> { update ->
            logger.info { "Update character ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")
            val race = RaceId(formParameters.getOrFail("race").toInt())
            val gender = Gender.valueOf(formParameters.getOrFail("gender"))
            val culture = formParameters.getOrFail("culture")
                .toIntOrNull()
                ?.let { CultureId(it) }

            STORE.dispatch(UpdateCharacter(update.id, name, race, gender, culture))

            call.respondRedirect(href(call, update.id))
        }
        get<Characters.Languages.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}'s languages" }

            val state = STORE.getState()
            val character = state.characters.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, character)
            }
        }
        post<Characters.Languages.Update> { update ->
            logger.info { "Update character ${update.id.value}'s languages" }

            val formParameters = call.receiveParameters()
            val languageParam = formParameters["language"]

            if (!languageParam.isNullOrEmpty()) {
                val language = LanguageId(languageParam.toInt())
                val level = ComprehensionLevel.valueOf(formParameters.getOrFail("level"))

                STORE.dispatch(AddLanguage(update.id, language, level))
            }

            val removeList = formParameters.getAll("remove")?.map { LanguageId(it.toInt()) }

            if (removeList != null) {
                STORE.dispatch(RemoveLanguages(update.id, removeList.toSet()))
            }

            call.respondRedirect(href(call, update.id))
        }
    }
}

private fun HTML.showAllCharacters(call: ApplicationCall) {
    val characters = STORE.getState().characters
    val count = characters.getSize()
    val createLink = call.application.href(Characters.New(Characters()))

    simpleHtml("Characters") {
        field("Count", count.toString())
        listElements(characters.getAll()) { character ->
            link(call, character)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = call.application.href(Characters())
    val deleteLink = call.application.href(Characters.Delete(character.id))
    val editLink = call.application.href(Characters.Edit(character.id))
    val editLanguagesLink = call.application.href(Characters.Languages.Edit(character.id))
    val inventedLanguages = state.getInventedLanguages(character.id)

    simpleHtml("Character: ${character.name}") {
        field("Id", character.id.value.toString())
        field("Race") {
            link(call, state, character.race)
        }
        field("Gender", character.gender.toString())
        if (character.culture != null) {
            field("Culture") {
                link(call, state, character.culture)
            }
        }
        if (character.languages.isNotEmpty()) {
            field("Known Languages") {
                showMap(character.languages) { id, level ->
                    link(call, state, id)
                    +": $level"
                }
            }
        }
        if (inventedLanguages.isNotEmpty()) {
            field("Invented Languages") {
                listElements(inventedLanguages) { language ->
                    link(call, language)
                }
            }
        }
        p { a(editLink) { +"Edit" } }
        p { a(editLanguagesLink) { +"Edit Languages" } }
        p { a(deleteLink) { +"Delete" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = href(call, character.id)
    val updateLink = call.application.href(Characters.Update(character.id))

    simpleHtml("Edit Character: ${character.name}") {
        field("Id", character.id.value.toString())
        form {
            field("Name") {
                textInput(name = "name") {
                    value = character.name
                }
            }
            field("Race") {
                select {
                    id = "race"
                    name = "race"
                    state.races.getAll().forEach { race ->
                        option {
                            label = race.name
                            value = race.id.value.toString()
                            selected = race.id == character.race
                        }
                    }
                }
            }
            selectEnum("Gender", "gender", Gender.entries) { gender ->
                label = gender.toString()
                value = gender.toString()
                selected = character.gender == gender
            }
            field("Culture") {
                select {
                    id = "culture"
                    name = "culture"
                    option {
                        label = "No culture"
                        value = ""
                        selected = character.culture == null
                    }
                    state.cultures.getAll().forEach { culture ->
                        option {
                            label = culture.name
                            value = culture.id.value.toString()
                            selected = culture.id == character.culture
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

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = href(call, character.id)
    val updateLink = call.application.href(Characters.Languages.Update(character.id))

    simpleHtml("Edit Languages: ${character.name}") {
        form {
            field("Language") {
                select {
                    id = "language"
                    name = "language"
                    option {
                        label = ""
                        value = ""
                        selected = true
                    }
                    state.getPossibleLanguages(character.id).forEach { language ->
                        option {
                            label = language.name
                            value = language.id.value.toString()
                        }
                    }
                }
            }
            selectEnum("Comprehension Level", "level", ComprehensionLevel.entries) { level ->
                label = level.toString()
                value = level.toString()
                selected = level == ComprehensionLevel.Native
            }
            field("Languages to Remove") {
                character.languages.keys.forEach { id ->
                    val language = state.languages.getOrThrow(id)
                    p {
                        checkBoxInput {
                            name = "remove"
                            value = language.id.value.toString()
                            +language.name
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