package at.orchaldir.gm.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

private const val TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        staticFiles("/static", File("static"))
        get("/") {
            logger.info { "Root" }

            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +TITLE }
                    link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
                }
                body {
                    h1 { +TITLE }
                    p { +"Work in progress" }
                }
            }
        }
    }
}
