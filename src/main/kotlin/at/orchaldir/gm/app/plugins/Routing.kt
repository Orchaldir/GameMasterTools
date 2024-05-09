package at.orchaldir.gm.app.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        get("/") {
            logger.info { "Root" }
            call.respondText("Hello World!")
        }
    }
}
