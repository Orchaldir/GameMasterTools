package at.orchaldir.gm.app

import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.character.*
import at.orchaldir.gm.app.routes.character.title.configureTitleRouting
import at.orchaldir.gm.app.routes.culture.configureCultureRouting
import at.orchaldir.gm.app.routes.culture.configureFashionRouting
import at.orchaldir.gm.app.routes.culture.configureLanguageRouting
import at.orchaldir.gm.app.routes.economy.configureBusinessRouting
import at.orchaldir.gm.app.routes.economy.configureJobRouting
import at.orchaldir.gm.app.routes.economy.configureMaterialRouting
import at.orchaldir.gm.app.routes.economy.configureStandardOfLivingRouting
import at.orchaldir.gm.app.routes.economy.money.configureCurrencyRouting
import at.orchaldir.gm.app.routes.economy.money.configureCurrencyUnitRouting
import at.orchaldir.gm.app.routes.item.*
import at.orchaldir.gm.app.routes.magic.configureMagicTraditionRouting
import at.orchaldir.gm.app.routes.magic.configureSpellGroupRouting
import at.orchaldir.gm.app.routes.magic.configureSpellRouting
import at.orchaldir.gm.app.routes.organization.configureOrganizationRouting
import at.orchaldir.gm.app.routes.race.configureRaceAppearanceRouting
import at.orchaldir.gm.app.routes.race.configureRaceRouting
import at.orchaldir.gm.app.routes.realm.*
import at.orchaldir.gm.app.routes.religion.configureDomainRouting
import at.orchaldir.gm.app.routes.religion.configureGodRouting
import at.orchaldir.gm.app.routes.religion.configurePantheonRouting
import at.orchaldir.gm.app.routes.time.configureCalendarRouting
import at.orchaldir.gm.app.routes.time.configureHolidayRouting
import at.orchaldir.gm.app.routes.time.configureTimeRouting
import at.orchaldir.gm.app.routes.utls.configureDataSourceRouting
import at.orchaldir.gm.app.routes.utls.configureFontRouting
import at.orchaldir.gm.app.routes.utls.configureNameListRouting
import at.orchaldir.gm.app.routes.utls.configureQuoteRouting
import at.orchaldir.gm.app.routes.world.*
import at.orchaldir.gm.app.routes.world.town.*
import at.orchaldir.gm.core.action.LoadData
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.redux.DefaultStore
import at.orchaldir.gm.utils.redux.middleware.LogAction
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import mu.KotlinLogging

val STORE = DefaultStore(State(), REDUCER, listOf(LogAction()))
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Command line args: $args" }

    STORE.dispatch(LoadData(args[0]))

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Resources)
    configureSerialization()
    configureRouting()
    configureStatusPages()
    configureDataRouting()
    configureAbstractBuildingEditorRouting()
    configureArchitecturalStyleRouting()
    configureArticleRouting()
    configureBattleRouting()
    configureBuildingRouting()
    configureBuildingEditorRouting()
    configureBusinessRouting()
    configureCatastropheRouting()
    configureCharacterRouting()
    configureCurrencyRouting()
    configureCurrencyUnitRouting()
    configureEquipmentMapRouting()
    configureCharacterLanguageRouting()
    configureCharacterRelationshipRouting()
    configureAppearanceRouting()
    configureCalendarRouting()
    configureCultureRouting()
    configureDataSourceRouting()
    configureDomainRouting()
    configureEquipmentRouting()
    configureFashionRouting()
    configureFontRouting()
    configureGodRouting()
    configureHolidayRouting()
    configureEquipmentRouting()
    configureJobRouting()
    configureLanguageRouting()
    configureLegalCodeRouting()
    configureMagicTraditionRouting()
    configureMaterialRouting()
    configureMoonRouting()
    configureRegionRouting()
    configureNameListRouting()
    configureOrganizationRouting()
    configurePantheonRouting()
    configurePeriodicalRouting()
    configurePeriodicalIssueRouting()
    configurePersonalityRouting()
    configurePlaneRouting()
    configureQuoteRouting()
    configureRaceRouting()
    configureRaceAppearanceRouting()
    configureRealmRouting()
    configureRiverRouting()
    configureSpellRouting()
    configureSpellGroupRouting()
    configureStandardOfLivingRouting()
    configureTerrainRouting()
    configureTimeRouting()
    configureStreetRouting()
    configureStreetTemplateRouting()
    configureStreetEditorRouting()
    configureTextRouting()
    configureTitleRouting()
    configureTownRouting()
    configureTownMapRouting()
    configureTreatyRouting()
    configureUniformRouting()
    configureWarRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
