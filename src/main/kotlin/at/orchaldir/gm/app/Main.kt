package at.orchaldir.gm.app

import at.orchaldir.gm.app.plugins.configureCharacterRouting
import at.orchaldir.gm.app.plugins.configureCultureRouting
import at.orchaldir.gm.app.plugins.configureRouting
import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.model.character.Race
import at.orchaldir.gm.core.model.character.RaceId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
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
    configureCharacterRouting()
    configureCultureRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun initStore(): DefaultStore<Action, State> {
    val state = State(
        Storage(CharacterId(0)),
        Storage(CultureId(0)),
        Storage(listOf(Race(RaceId(0), "Human"))),
    )
    return DefaultStore(state, REDUCER, listOf(LogAction()))
}
