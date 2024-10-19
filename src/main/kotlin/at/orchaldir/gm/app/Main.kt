package at.orchaldir.gm.app

import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.character.*
import at.orchaldir.gm.app.routes.race.configureRaceAppearanceRouting
import at.orchaldir.gm.app.routes.race.configureRaceRouting
import at.orchaldir.gm.app.routes.world.*
import at.orchaldir.gm.app.routes.world.town.configureBuildingEditorRouting
import at.orchaldir.gm.app.routes.world.town.configureStreetEditorRouting
import at.orchaldir.gm.app.routes.world.town.configureTerrainRouting
import at.orchaldir.gm.app.routes.world.town.configureTownRouting
import at.orchaldir.gm.core.action.Action
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

val STORE = initStore()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Resources)
    configureSerialization()
    configureRouting()
    configureStatusPages()
    configureBuildingRouting()
    configureBuildingEditorRouting()
    configureCharacterRouting()
    configureCharacterLanguageRouting()
    configureCharacterRelationshipRouting()
    configureAppearanceRouting()
    configureCalendarRouting()
    configureCultureRouting()
    configureEquipmentRouting()
    configureFashionRouting()
    configureHolidayRouting()
    configureItemTemplateRouting()
    configureLanguageRouting()
    configureMaterialRouting()
    configureMoonRouting()
    configureMountainRouting()
    configureNameListRouting()
    configurePersonalityRouting()
    configureRaceRouting()
    configureRaceAppearanceRouting()
    configureRiverRouting()
    configureTerrainRouting()
    configureTimeRouting()
    configureStreetRouting()
    configureStreetTypeRouting()
    configureStreetEditorRouting()
    configureTownRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun initStore(): DefaultStore<Action, State> {
    val state = State.load("data")

    return DefaultStore(state, REDUCER, listOf(LogAction()))
}
