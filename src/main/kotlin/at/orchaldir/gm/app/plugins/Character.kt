package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/characters")
class Characters {
    @Resource("details")
    class Details(val parent: Characters = Characters(), val id: CharacterId)

    @Resource("new")
    class New(val parent: Characters = Characters())

    @Resource("delete")
    class Delete(val parent: Characters = Characters(), val id: CharacterId)

    @Resource("edit")
    class Edit(val parent: Characters = Characters(), val id: CharacterId)

    @Resource("update")
    class Update(
        val parent: Characters = Characters(),
        val id: CharacterId,
    )
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

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterDetails(call, details.id)
            }
        }
        get<Characters.New> {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterEditor(call, STORE.getState().characters.lastId)
            }
        }
        get<Characters.Delete> { delete ->
            logger.info { "Delete character ${delete.id.value}" }

            STORE.dispatch(DeleteCharacter(delete.id))

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters(call)
            }
        }
        get<Characters.Edit> { edit ->
            logger.info { "Get editor for character ${edit.id.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val character = state.characters.get(edit.id)

                if (character != null) {
                    showCharacterEditor(call, state, character)
                } else {
                    showAllCharacters(call)
                }
            }
        }
        post<Characters.Update> { update ->
            logger.info { "Update character ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")
            val gender = Gender.valueOf(formParameters.getOrFail("gender"))
            val culture = formParameters.getOrFail("culture")
                .toIntOrNull()
                ?.let { CultureId(it) }

            STORE.dispatch(UpdateCharacter(update.id, name, RaceId(0), gender, culture))

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterDetails(call, update.id)
            }
        }
    }
}

private fun HTML.showAllCharacters(call: ApplicationCall) {
    val characters = STORE.getState().characters
    val count = characters.getSize()
    val createLink = call.application.href(Characters.New(Characters()))

    simpleHtml("Characters") {
        field("Count", count.toString())
        characterList(call, characters.getAll())
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    id: CharacterId,
) {
    val state = STORE.getState()
    val character = state.characters.get(id)

    if (character != null) {
        showCharacterDetails(call, state, character)
    } else {
        showAllCharacters(call)
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = call.application.href(Characters())
    val deleteLink = call.application.href(Characters.Delete(Characters(), character.id))
    val editLink = call.application.href(Characters.Edit(Characters(), character.id))
    val race = state.races.get(character.race)?.name ?: "Unknown"
    val raceLink = call.application.href(Races.Details(Races(), character.race))

    simpleHtml("Character: ${character.name}") {
        field("Id", character.id.value.toString())
        fieldLink("Race", raceLink, race)
        field("Gender", character.gender.toString())
        if (character.culture != null) {
            val culture = state.cultures.get(character.culture)?.name ?: "Unknown"
            val cultureLink = call.application.href(Cultures.Details(Cultures(), character.culture))
            fieldLink("Culture", cultureLink, culture)
        }
        p { a(editLink) { +"Edit" } }
        p { a(deleteLink) { +"Delete" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    id: CharacterId,
) {
    val state = STORE.getState()
    val character = state.characters.get(id)

    if (character != null) {
        showCharacterEditor(call, state, character)
    } else {
        showAllCharacters(call)
    }
}

private fun HTML.showCharacterEditor(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val backLink = call.application.href(Characters())
    val updateLink = call.application.href(Characters.Update(Characters(), character.id))

    simpleHtml("Edit Character: ${character.name}") {
        field("Id", character.id.value.toString())
        form {
            p {
                b { +"Name: " }
                textInput(name = "name") {
                    value = character.name
                }
            }
            p {
                b { +"Gender: " }
                select {
                    id = "gender"
                    name = "gender"
                    Gender.entries.forEach { gender ->
                        option {
                            label = gender.toString()
                            value = gender.toString()
                            selected = character.gender == gender
                        }
                    }
                }
            }
            p {
                b { +"Culture: " }
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
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}