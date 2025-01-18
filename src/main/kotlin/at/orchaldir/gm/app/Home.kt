package at.orchaldir.gm.app

import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.fieldStorageLink
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.app.routes.economy.BusinessRoutes
import at.orchaldir.gm.app.routes.economy.JobRoutes
import at.orchaldir.gm.app.routes.item.ItemTemplateRoutes
import at.orchaldir.gm.app.routes.item.TextRoutes
import at.orchaldir.gm.app.routes.race.RaceRoutes
import at.orchaldir.gm.app.routes.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.app.routes.world.*
import at.orchaldir.gm.app.routes.world.town.TownRoutes
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

const val APP_TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")
        get("/") {
            logger.info { "Root" }
            val state = STORE.getState()
            val timeLink = call.application.href(TimeRoutes())
            val eventsLink = call.application.href(TimeRoutes.ShowEvents())

            call.respondHtml(HttpStatusCode.OK) {
                simpleHtml(APP_TITLE) {
                    h2 { +"Elements" }
                    fieldStorageLink(call, state.getCharacterStorage(), CharacterRoutes.All())
                    fieldStorageLink(call, state.getCalendarStorage(), CalendarRoutes())
                    fieldStorageLink(call, state.getCultureStorage(), CultureRoutes())
                    fieldStorageLink(call, state.getFashionStorage(), FashionRoutes())
                    fieldStorageLink(call, state.getHolidayStorage(), HolidayRoutes())
                    fieldStorageLink(call, state.getLanguageStorage(), LanguageRoutes())
                    fieldStorageLink(call, state.getMaterialStorage(), MaterialRoutes())
                    fieldStorageLink(call, state.getNameListStorage(), NameListRoutes())
                    fieldStorageLink(call, state.getPersonalityTraitStorage(), PersonalityTraitRoutes())
                    fieldStorageLink(call, state.getRaceStorage(), RaceRoutes())
                    fieldStorageLink(call, state.getRaceAppearanceStorage(), AppearanceRoutes())
                    h3 { +"Items" }
                    fieldStorageLink(call, state.getTextStorage(), TextRoutes.All())
                    fieldStorageLink(call, state.getItemTemplateStorage(), ItemTemplateRoutes())
                    h3 { +"Economy" }
                    fieldStorageLink(call, state.getBusinessStorage(), BusinessRoutes.All())
                    fieldStorageLink(call, state.getJobStorage(), JobRoutes())
                    h3 { +"World" }
                    fieldStorageLink(call, state.getArchitecturalStyleStorage(), ArchitecturalStyleRoutes.All())
                    fieldStorageLink(call, state.getBuildingStorage(), BuildingRoutes.All())
                    fieldStorageLink(call, state.getMoonStorage(), MoonRoutes())
                    fieldStorageLink(call, state.getMountainStorage(), MountainRoutes())
                    fieldStorageLink(call, state.getRiverStorage(), RiverRoutes())
                    fieldStorageLink(call, state.getStreetStorage(), StreetRoutes())
                    fieldStorageLink(call, state.getStreetTemplateStorage(), StreetTemplateRoutes())
                    fieldStorageLink(call, state.getTownStorage(), TownRoutes())
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
