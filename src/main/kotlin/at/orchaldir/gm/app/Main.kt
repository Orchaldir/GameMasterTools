package at.orchaldir.gm.app

import at.orchaldir.gm.app.plugins.configureCharacterRouting
import at.orchaldir.gm.app.plugins.configureRouting
import at.orchaldir.gm.core.action.CharacterAction
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.reducer.character.CREATE_CHARACTER
import at.orchaldir.gm.utils.redux.DefaultStore
import at.orchaldir.gm.utils.redux.Reducer
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
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun initStore(): DefaultStore<CharacterAction, State> {
    val reduce: Reducer<CharacterAction, State> = { state, action ->
        when (action) {
            is CreateCharacter -> CREATE_CHARACTER(state, action)
        }
    }
    val state = State(mutableMapOf(CharacterId(0) to Character(CharacterId(0), "Test")))
    return DefaultStore(state, reduce, listOf(LogAction()))
}
