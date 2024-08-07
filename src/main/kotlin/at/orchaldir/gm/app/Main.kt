package at.orchaldir.gm.app

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.*
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
    configureCharacterRouting()
    configureCharacterLanguageRouting()
    configureCharacterRelationshipRouting()
    configureAppearanceRouting()
    configureCalendarRouting()
    configureCultureRouting()
    configureEquipmentRouting()
    configureFashionRouting()
    configureItemTemplateRouting()
    configureLanguageRouting()
    configureMaterialRouting()
    configureNameListRouting()
    configurePersonalityRouting()
    configureRaceRouting()
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
