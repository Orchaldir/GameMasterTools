package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/characters")
class Characters {
    @Resource("details")
    class Details(val parent: Characters = Characters(), val id: CharacterId)

    @Resource("new")
    class New(val parent: Characters = Characters())
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

            val character = STORE.getState().characters.get(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                if (character != null) {
                    showCharacterDetails(call, character)
                } else {
                    showAllCharacters(call)
                }
            }
        }
        get<Characters.New> {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters(call)
            }
        }
    }
}

private fun HTML.showAllCharacters(call: ApplicationCall) {
    val characters = STORE.getState().characters
    val count = characters.getSize()
    val createLink: String = call.application.href(Characters.New(Characters()))

    simpleHtml("Characters") {
        field("Count", count.toString())
        ul {
            characters.getAll().forEach { character ->
                li {
                    val characterLink = call.application.href(Characters.Details(Characters(), character.id))
                    a(characterLink) { +character.name }
                }
            }
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCharacterDetails(
    call: ApplicationCall,
    character: Character
) {
    val backLink: String = call.application.href(Characters())

    simpleHtml("Character: ${character.name}") {
        field("Id", character.id.value.toString())
        field("Gender", character.name)
        p { a(backLink) { +"Back" } }
    }
}