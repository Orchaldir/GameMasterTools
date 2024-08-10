package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldStorageLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.plugins.character.Characters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.h2
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

const val TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")
        get("/") {
            logger.info { "Root" }
            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                simpleHtml(TITLE) {
                    h2 { +"Elements" }
                    fieldStorageLink(call, state.characters, Characters())
                    fieldStorageLink(call, state.calendars, Calendars())
                    fieldStorageLink(call, state.cultures, Cultures())
                    fieldStorageLink(call, state.fashion, Fashions())
                    fieldStorageLink(call, state.itemTemplates, ItemTemplates())
                    fieldStorageLink(call, state.languages, Languages())
                    fieldStorageLink(call, state.materials, Materials())
                    fieldStorageLink(call, state.nameLists, NameLists())
                    fieldStorageLink(call, state.personalityTraits, Personality())
                    fieldStorageLink(call, state.races, Races())
                    h2 { +"Time" }
                    field("Default Calendar") {
                        link(call, state, state.time.defaultCalendar)
                    }
                    field(state, "Current Date", state.time.currentDate)
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
