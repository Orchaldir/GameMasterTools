package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
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
            val count = STORE.getState().characters.size

            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +TITLE }
                    link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
                }
                body {
                    h1 { +"Characters" }
                    p { +"Count: $count" }
                }
            }
        }
    }
}
