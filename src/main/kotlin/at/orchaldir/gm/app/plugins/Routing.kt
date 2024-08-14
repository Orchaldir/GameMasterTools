package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.plugins.character.Characters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

const val TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")
        get("/") {
            logger.info { "Root" }
            val state = STORE.getState()
            val timeLink = call.application.href(TimeRoutes())

            call.respondHtml(HttpStatusCode.OK) {
                simpleHtml(TITLE) {
                    h2 { +"Elements" }
                    fieldStorageLink(call, state.getCharacterStorage(), Characters())
                    fieldStorageLink(call, state.getCalendarStorage(), Calendars())
                    fieldStorageLink(call, state.getCultureStorage(), Cultures())
                    fieldStorageLink(call, state.getFashionStorage(), Fashions())
                    fieldStorageLink(call, state.getItemTemplateStorage(), ItemTemplates())
                    fieldStorageLink(call, state.getLanguageStorage(), Languages())
                    fieldStorageLink(call, state.getMaterialStorage(), Materials())
                    fieldStorageLink(call, state.getNameListStorage(), NameLists())
                    fieldStorageLink(call, state.getPersonalityTraitStorage(), Personality())
                    fieldStorageLink(call, state.getRaceStorage(), Races())
                    h2 { +"Data" }
                    ul {
                        li {
                            p { a(timeLink) { +"Time" } }
                        }
                    }
                }
            }
        }
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "Unknown error")
        }
    }
}
