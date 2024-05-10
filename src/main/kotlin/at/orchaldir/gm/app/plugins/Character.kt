package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateCharacter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureCharacterRouting() {
    routing {
        get("/characters") {
            logger.info { "Get all characters" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters()
            }
        }
        get("/characters/new") {
            logger.info { "Add new character" }

            STORE.dispatch(CreateCharacter)

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacters()
            }
        }
    }
}

private fun HTML.showAllCharacters() {
    val count = STORE.getState().characters.size

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
        p { a("/characters/new") { +"Add" } }
        p { a("/") { +"Back" } }
    }
}
