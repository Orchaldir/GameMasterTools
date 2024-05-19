package at.orchaldir.gm.app

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.EvolvedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
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
    configureStatusPages()
    configureCharacterRouting()
    configureCultureRouting()
    configureLanguageRouting()
    configureRaceRouting()
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
        Storage(
            listOf(
                Language(LanguageId(0), "Old Common"),
                Language(LanguageId(1), "Common", EvolvedLanguage(LanguageId(0)))
            )
        ),
        Storage(PersonalityTraitId(0)),
        Storage(listOf(Race(RaceId(0), "Human"))),
    )
    return DefaultStore(state, REDUCER, listOf(LogAction()))
}
