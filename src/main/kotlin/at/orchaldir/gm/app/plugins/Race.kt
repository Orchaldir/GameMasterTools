package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Race
import at.orchaldir.gm.core.model.character.RaceId
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/races")
class Races {
    @Resource("details")
    class Details(val id: RaceId, val parent: Races = Races())

    @Resource("new")
    class New(val parent: Races = Races())

    @Resource("delete")
    class Delete(val id: RaceId, val parent: Races = Races())

    @Resource("edit")
    class Edit(val id: RaceId, val parent: Races = Races())

    @Resource("update")
    class Update(val id: RaceId, val parent: Races = Races())
}

fun Application.configureRaceRouting() {
    routing {
        get<Races> {
            logger.info { "Get all races" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRaces(call)
            }
        }
        get<Races.Details> { details ->
            logger.info { "Get details of race ${details.id.value}" }

            val state = STORE.getState()
            val race = state.races.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceDetails(call, state, race)
            }
        }
        get<Races.New> {
            logger.info { "Add new race" }

            STORE.dispatch(CreateRace)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, STORE.getState().races.lastId)
            }

            call.respondRedirect(call.application.href(Races.Edit(STORE.getState().races.lastId)))
        }
        get<Races.Delete> { delete ->
            logger.info { "Delete race ${delete.id.value}" }

            STORE.dispatch(DeleteRace(delete.id))

            call.respondRedirect(call.application.href(Races()))
        }
        get<Races.Edit> { edit ->
            logger.info { "Get editor for race ${edit.id.value}" }

            val race = STORE.getState().races.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, race)
            }
        }
        post<Races.Update> { update ->
            logger.info { "Update race ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")

            STORE.dispatch(UpdateRace(update.id, name))

            call.respondRedirect(href(call, update.id))
        }
    }
}

private fun HTML.showAllRaces(call: ApplicationCall) {
    val races = STORE.getState().races
    val count = races.getSize()
    val createLink = call.application.href(Races.New(Races()))

    simpleHtml("Races") {
        field("Count", count.toString())
        showList(races.getAll()) { race ->
            link(call, race)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showRaceDetails(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val backLink = href(call, race.id)
    val deleteLink = call.application.href(Races.Delete(race.id))
    val editLink = call.application.href(Races.Edit(race.id))

    simpleHtml("Race: ${race.name}") {
        field("Id", race.id.value.toString())
        field("Name", race.name)
        field("Characters") {
            showList(state.getCharacters(race.id)) { character ->
                link(call, character)
            }
        }
        p { a(editLink) { +"Edit" } }

        if (state.canDelete(race.id)) {
            p { a(deleteLink) { +"Delete" } }
        }

        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    id: RaceId,
) {
    val race = STORE.getState().races.get(id)

    if (race != null) {
        showRaceEditor(call, race)
    } else {
        showAllRaces(call)
    }
}

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    race: Race,
) {
    val backLink = call.application.href(Races())
    val updateLink = call.application.href(Races.Update(race.id))

    simpleHtml("Edit Race: ${race.name}") {
        field("Id", race.id.value.toString())
        form {
            field("Name") {
                textInput(name = "name") {
                    value = race.name
                }
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}