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
    configurePersonalityRouting()
    configureRaceRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun initStore(): DefaultStore<Action, State> {
    var t = 0

    val state = State(
        Storage(CharacterId(0), "Character"),
        Storage(CultureId(0), "Culture"),
        Storage(
            listOf(
                Language(LanguageId(0), "Old Common"),
                Language(LanguageId(1), "Common", EvolvedLanguage(LanguageId(0)))
            ),
            "Language",
        ),
        Storage(
            listOf(
                create(t++, "Honest"),
                create(t++, "Deceitful"),
                create(t++, "Brave"),
                create(t++, "Cowardly"),
                create(t++, "Content"),
                create(t++, "Ambitious"),
                create(t++, "Energetic"), // Active, Diligent
                create(t++, "Lazy"),
                create(t++, "Forgiving"),
                create(t++, "Vengeful"),
                create(t++, "Neat"),
                create(t++, "Sloppy"),
                create(t++, "Outgoing"),
                create(t++, "Shy"),
                create(t++, "Humble"),
                create(t++, "Arrogant"),
                create(t++, "Just"),
                create(t++, "Arbitrary"),
                create(t++, "Patient"),
                create(t++, "Impatient"),
                create(t++, "Temperate"),
                create(t++, "Gluttonous"),
                create(t++, "Trusting"),
                create(t++, "Paranoid"),
                create(t++, "Zealous"),
                create(t++, "Cynical"),
                create(t++, "Playful"),
                create(t++, "Serious"),
                create(t++, "Compassionate", 100),
                create(t++, "Callous", 100),
                create(t++, "Mean", 100),
                create(t++, "Sadistic", 100),
                create(t++, "Calm", 101),
                create(t++, "Excitable", 101),
                create(t++, "Grumpy", 101),
                create(t++, "Wrathful", 101), // hot headed
                create(t++, "Chaste", 102),
                create(t++, "Flirty", 102),
                create(t++, "Lustful", 102),
                create(t++, "Generous", 103),
                create(t++, "Frugal", 103),
                create(t++, "Greedy", 103),
            ),
            "Personality Trait"
        ),
        Storage(listOf(Race(RaceId(0), "Human")), "Race"),
    )
    return DefaultStore(state, REDUCER, listOf(LogAction()))
}

private fun create(id: Int, name: String, group: Int = id / 2) =
    PersonalityTrait(PersonalityTraitId(id), name, PersonalityTraitGroup(group))