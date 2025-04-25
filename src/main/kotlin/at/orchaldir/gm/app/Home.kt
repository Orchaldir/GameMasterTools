package at.orchaldir.gm.app

import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.fieldStorageLink
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.app.routes.culture.CultureRoutes
import at.orchaldir.gm.app.routes.culture.FashionRoutes
import at.orchaldir.gm.app.routes.economy.BusinessRoutes
import at.orchaldir.gm.app.routes.economy.JobRoutes
import at.orchaldir.gm.app.routes.economy.money.CurrencyRoutes
import at.orchaldir.gm.app.routes.item.*
import at.orchaldir.gm.app.routes.magic.SpellRoutes
import at.orchaldir.gm.app.routes.organization.OrganizationRoutes
import at.orchaldir.gm.app.routes.race.RaceRoutes
import at.orchaldir.gm.app.routes.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.app.routes.religion.DomainRoutes
import at.orchaldir.gm.app.routes.religion.GodRoutes
import at.orchaldir.gm.app.routes.religion.PantheonRoutes
import at.orchaldir.gm.app.routes.time.CalendarRoutes
import at.orchaldir.gm.app.routes.time.TimeRoutes
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
import kotlinx.html.h4
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
                    fieldStorageLink(call, state.getFontStorage(), FontRoutes.All())
                    fieldStorageLink(call, state.getOrganizationStorage(), OrganizationRoutes.All())
                    h3 { +"Characters" }
                    fieldStorageLink(call, state.getCharacterStorage(), CharacterRoutes.All())
                    fieldStorageLink(call, state.getPersonalityTraitStorage(), PersonalityTraitRoutes())
                    fieldStorageLink(call, state.getRaceStorage(), RaceRoutes.All())
                    fieldStorageLink(call, state.getRaceAppearanceStorage(), AppearanceRoutes())
                    h3 { +"Cultures" }
                    fieldStorageLink(call, state.getCultureStorage(), CultureRoutes())
                    fieldStorageLink(call, state.getFashionStorage(), FashionRoutes())
                    fieldStorageLink(call, state.getLanguageStorage(), LanguageRoutes())
                    fieldStorageLink(call, state.getNameListStorage(), NameListRoutes())
                    h3 { +"Items" }
                    fieldStorageLink(call, state.getEquipmentStorage(), EquipmentRoutes())
                    fieldStorageLink(call, state.getMaterialStorage(), MaterialRoutes.All())
                    fieldStorageLink(call, state.getTextStorage(), TextRoutes.All())
                    h4 { +"Periodicals" }
                    fieldStorageLink(call, state.getArticleStorage(), ArticleRoutes.All())
                    fieldStorageLink(call, state.getPeriodicalStorage(), PeriodicalRoutes.All())
                    fieldStorageLink(call, state.getPeriodicalIssueStorage(), PeriodicalIssueRoutes.All())
                    h3 { +"Economy" }
                    fieldStorageLink(call, state.getBusinessStorage(), BusinessRoutes.All())
                    fieldStorageLink(call, state.getCurrencyStorage(), CurrencyRoutes.All())
                    fieldStorageLink(call, state.getJobStorage(), JobRoutes())
                    h3 { +"Magic" }
                    fieldStorageLink(call, state.getSpellStorage(), SpellRoutes.All())
                    h3 { +"Religion" }
                    fieldStorageLink(call, state.getDomainStorage(), DomainRoutes.All())
                    fieldStorageLink(call, state.getGodStorage(), GodRoutes.All())
                    fieldStorageLink(call, state.getPantheonStorage(), PantheonRoutes.All())
                    h3 { +"Time" }
                    fieldStorageLink(call, state.getCalendarStorage(), CalendarRoutes())
                    fieldStorageLink(call, state.getHolidayStorage(), HolidayRoutes())
                    action(timeLink, "Time")
                    action(eventsLink, "Events")
                    h3 { +"World" }
                    fieldStorageLink(call, state.getArchitecturalStyleStorage(), ArchitecturalStyleRoutes.All())
                    fieldStorageLink(call, state.getBuildingStorage(), BuildingRoutes.All())
                    fieldStorageLink(call, state.getMoonStorage(), MoonRoutes())
                    fieldStorageLink(call, state.getMountainStorage(), MountainRoutes())
                    fieldStorageLink(call, state.getPlaneStorage(), PlaneRoutes.All())
                    fieldStorageLink(call, state.getRiverStorage(), RiverRoutes())
                    fieldStorageLink(call, state.getStreetStorage(), StreetRoutes())
                    fieldStorageLink(call, state.getStreetTemplateStorage(), StreetTemplateRoutes())
                    fieldStorageLink(call, state.getTownStorage(), TownRoutes())
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
