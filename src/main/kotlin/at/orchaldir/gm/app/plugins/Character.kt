package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateCharacter
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
    class Details(val parent: Characters = Characters(), val id: Int)

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
            logger.info { "Get details of character ${details.id}" }

            val character = STORE.getState().characters[CharacterId(details.id)]

            if (character != null) {
                val backLink: String = call.application.href(Characters())

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +TITLE }
                        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
                    }
                    body {
                        h1 { +"Character: ${character.name}" }
                        p {
                            b { +"Id: " }
                            +"${character.id.id}"
                        }
                        p {
                            b { +"Gender: " }
                            +"${character.gender}"
                        }
                        p { a(backLink) { +"Back" } }
                    }
                }
            } else {
                call.respondHtml(HttpStatusCode.OK) {
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
    val count = characters.size
    val createLink: String = call.application.href(Characters.New(Characters()))

    head {
        title { +TITLE }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
    }
    body {
        h1 { +"Characters" }
        p {
            b { +"Count: " }
            +"$count"
        }
        ul {
            characters.values.forEach { character ->
                li {
                    val characterLink = call.application.href(Characters.Details(Characters(), character.id.id))
                    a(characterLink) { +character.name }
                }
            }
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}
