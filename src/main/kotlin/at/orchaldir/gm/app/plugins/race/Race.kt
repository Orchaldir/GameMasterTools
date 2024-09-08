package at.orchaldir.gm.app.plugins.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.GENDER
import at.orchaldir.gm.app.parse.NAME
import at.orchaldir.gm.app.parse.parseRace
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureRaceRouting() {
    routing {
        get<RaceRoutes> {
            logger.info { "Get all races" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRaces(call)
            }
        }
        get<RaceRoutes.Details> { details ->
            logger.info { "Get details of race ${details.id.value}" }

            val state = STORE.getState()
            val race = state.getRaceStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceDetails(call, state, race)
            }
        }
        get<RaceRoutes.New> {
            logger.info { "Add new race" }

            STORE.dispatch(CreateRace)

            call.respondRedirect(call.application.href(RaceRoutes.Edit(STORE.getState().getRaceStorage().lastId)))

            STORE.getState().save()
        }
        get<RaceRoutes.Delete> { delete ->
            logger.info { "Delete race ${delete.id.value}" }

            STORE.dispatch(DeleteRace(delete.id))

            call.respondRedirect(call.application.href(RaceRoutes()))

            STORE.getState().save()
        }
        get<RaceRoutes.Edit> { edit ->
            logger.info { "Get editor for race ${edit.id.value}" }

            val race = STORE.getState().getRaceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, race)
            }
        }
        post<RaceRoutes.Preview> { preview ->
            logger.info { "Get preview for race ${preview.id.value}" }

            val race = parseRace(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, race)
            }
        }
        post<RaceRoutes.Update> { update ->
            logger.info { "Update race ${update.id.value}" }

            val race = parseRace(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRace(race))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllRaces(call: ApplicationCall) {
    val races = STORE.getState().getRaceStorage().getAll().sortedBy { it.name }
    val count = races.size
    val createLink = call.application.href(RaceRoutes.New())

    simpleHtml("Races") {
        field("Count", count.toString())
        showList(races) { race ->
            link(call, race)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showRaceDetails(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val backLink = call.application.href(RaceRoutes())
    val deleteLink = call.application.href(RaceRoutes.Delete(race.id))
    val editLink = call.application.href(RaceRoutes.Edit(race.id))

    simpleHtml("Race: ${race.name}") {
        field("Id", race.id.value.toString())
        field("Name", race.name)
        showRarityMap("Gender", race.genders)
        showLifeStages(race)
        h2 { +"Characters" }
        showList(state.getCharacters(race.id)) { character ->
            link(call, state, character)
        }
        action(editLink, "Edit")

        if (state.canDelete(race.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun BODY.showLifeStages(
    race: Race,
) {
    h2 { +"Life Stages" }
}

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    race: Race,
) {
    val backLink = call.application.href(RaceRoutes.Details(race.id))
    val previewLink = call.application.href(RaceRoutes.Preview(race.id))
    val updateLink = call.application.href(RaceRoutes.Update(race.id))

    simpleHtml("Edit Race: ${race.name}") {
        field("Id", race.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = NAME) {
                    value = race.name
                }
            }
            selectRarityMap("Gender", GENDER, race.genders)

            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}
