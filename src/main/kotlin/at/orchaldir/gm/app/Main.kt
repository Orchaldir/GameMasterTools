package at.orchaldir.gm.app

import at.orchaldir.gm.app.routes.character.configureAppearanceRouting
import at.orchaldir.gm.app.routes.character.configureCharacterRelationshipRouting
import at.orchaldir.gm.app.routes.character.configureCharacterRouting
import at.orchaldir.gm.app.routes.character.configureCharacterTemplateRouting
import at.orchaldir.gm.app.routes.character.title.configureTitleRouting
import at.orchaldir.gm.app.routes.configureDataRouting
import at.orchaldir.gm.app.routes.culture.configureCultureRouting
import at.orchaldir.gm.app.routes.culture.configureFashionRouting
import at.orchaldir.gm.app.routes.culture.configureLanguageRouting
import at.orchaldir.gm.app.routes.economy.configureBusinessRouting
import at.orchaldir.gm.app.routes.economy.configureJobRouting
import at.orchaldir.gm.app.routes.economy.configureMaterialRouting
import at.orchaldir.gm.app.routes.economy.configureStandardOfLivingRouting
import at.orchaldir.gm.app.routes.economy.money.configureCurrencyRouting
import at.orchaldir.gm.app.routes.economy.money.configureCurrencyUnitRouting
import at.orchaldir.gm.app.routes.health.configureDiseaseRouting
import at.orchaldir.gm.app.routes.item.*
import at.orchaldir.gm.app.routes.magic.configureMagicTraditionRouting
import at.orchaldir.gm.app.routes.magic.configureSpellGroupRouting
import at.orchaldir.gm.app.routes.magic.configureSpellRouting
import at.orchaldir.gm.app.routes.organization.configureOrganizationRouting
import at.orchaldir.gm.app.routes.race.configureRaceAppearanceRouting
import at.orchaldir.gm.app.routes.race.configureRaceGroupRouting
import at.orchaldir.gm.app.routes.race.configureRaceRouting
import at.orchaldir.gm.app.routes.realm.*
import at.orchaldir.gm.app.routes.religion.configureDomainRouting
import at.orchaldir.gm.app.routes.religion.configureGodRouting
import at.orchaldir.gm.app.routes.religion.configurePantheonRouting
import at.orchaldir.gm.app.routes.rpg.*
import at.orchaldir.gm.app.routes.time.configureCalendarRouting
import at.orchaldir.gm.app.routes.time.configureHolidayRouting
import at.orchaldir.gm.app.routes.time.configureTimeRouting
import at.orchaldir.gm.app.routes.utls.*
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
    // elements
    configureAbstractBuildingEditorRouting()
    configureAppearanceRouting()
    configureArchitecturalStyleRouting()
    configureArmorTypeRouting()
    configureArticleRouting()
    configureBattleRouting()
    configureBuildingEditorRouting()
    configureBuildingRouting()
    configureBusinessRouting()
    configureCalendarRouting()
    configureCatastropheRouting()
    configureCharacterRelationshipRouting()
    configureCharacterRouting()
    configureCharacterTemplateRouting()
    configureCharacterTraitRouting()
    configureColorSchemeRouting()
    configureCultureRouting()
    configureCurrencyRouting()
    configureCurrencyUnitRouting()
    configureDamageTypeRouting()
    configureDataSourceRouting()
    configureDiseaseRouting()
    configureDistrictRouting()
    configureDomainRouting()
    configureEquipmentModifierRouting()
    configureEquipmentRouting()
    configureEquipmentRouting()
    configureFashionRouting()
    configureFontRouting()
    configureGodRouting()
    configureHolidayRouting()
    configureJobRouting()
    configureLanguageRouting()
    configureLegalCodeRouting()
    configureMagicTraditionRouting()
    configureMaterialRouting()
    configureMeleeWeaponTypeRouting()
    configureMoonRouting()
    configureNameListRouting()
    configureOrganizationRouting()
    configurePantheonRouting()
    configurePeriodicalIssueRouting()
    configurePeriodicalRouting()
    configurePlaneRouting()
    configureQuoteRouting()
    configureRaceAppearanceRouting()
    configureRaceGroupRouting()
    configureRaceRouting()
    configureRealmRouting()
    configureRegionRouting()
    configureRiverRouting()
    configureShieldTypeRouting()
    configureSpellGroupRouting()
    configureSpellRouting()
    configureStandardOfLivingRouting()
    configureStatisticRouting()
    configureStreetEditorRouting()
    configureStreetRouting()
    configureStreetTemplateRouting()
    configureTerrainRouting()
    configureTextRouting()
    configureTimeRouting()
    configureTitleRouting()
    configureTownMapRouting()
    configureTownRouting()
    configureTreatyRouting()
    configureUniformRouting()
    configureWarRouting()
    configureWorldRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
