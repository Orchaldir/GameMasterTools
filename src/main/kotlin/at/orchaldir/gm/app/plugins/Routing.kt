package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.fieldStorageLink
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.plugins.character.Characters
import at.orchaldir.gm.app.plugins.race.RaceRoutes
import at.orchaldir.gm.app.plugins.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.app.plugins.world.Moons
import at.orchaldir.gm.app.plugins.world.RiverRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.ul
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
            val eventsLink = call.application.href(TimeRoutes.ShowEvents())

            call.respondHtml(HttpStatusCode.OK) {
                simpleHtml(TITLE) {
                    h2 { +"Elements" }
                    fieldStorageLink(call, state.getCharacterStorage(), Characters())
                    fieldStorageLink(call, state.getCalendarStorage(), Calendars())
                    fieldStorageLink(call, state.getCultureStorage(), Cultures())
                    fieldStorageLink(call, state.getFashionStorage(), Fashions())
                    fieldStorageLink(call, state.getHolidayStorage(), Holidays())
                    fieldStorageLink(call, state.getItemTemplateStorage(), ItemTemplates())
                    fieldStorageLink(call, state.getLanguageStorage(), Languages())
                    fieldStorageLink(call, state.getMaterialStorage(), Materials())
                    fieldStorageLink(call, state.getNameListStorage(), NameLists())
                    fieldStorageLink(call, state.getPersonalityTraitStorage(), Personality())
                    fieldStorageLink(call, state.getRaceStorage(), RaceRoutes())
                    fieldStorageLink(call, state.getRaceAppearanceStorage(), AppearanceRoutes())
                    h3 { +"World" }
                    fieldStorageLink(call, state.getMoonStorage(), Moons())
                    fieldStorageLink(call, state.getRiverStorage(), RiverRoutes())
                    h2 { +"Data" }
                    ul {
                        li {
                            action(timeLink, "Time")
                        }
                        li {
                            action(eventsLink, "Events")
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
