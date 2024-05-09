package at.orchaldir.gm.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        get("/") {
            logger.info { "Root" }

            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +TITLE
                    }
                }
                body {
                    h1 {
                        +TITLE
                    }
                }
            }
        }
    }
}
