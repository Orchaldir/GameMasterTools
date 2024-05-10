package at.orchaldir.gm.app

import at.orchaldir.gm.core.action.CharacterAction
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.app.plugins.configureRouting
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.DefaultStore
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.middleware.LogAction
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

val STORE = initStore()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}

fun initStore(): DefaultStore<CharacterAction, State> {
    val state = State()
    val reduce: Reducer<CharacterAction, State> = { state, action ->
        when (action) {
            CreateCharacter -> TODO()
        }
    }
    return DefaultStore(state, reduce, listOf(LogAction()))
}
