package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.race.AppearanceOptions
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
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

private const val EAR_SHAPE = "ear_shape"
private const val SCALE_COLOR = "scale_color"
private const val NORMAL_SKIN_COLOR = "normal_skin_color"
private const val EXOTIC_SKIN_COLOR = "exotic_skin_color"

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

            val race = parseRace(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRace(race))

            call.respondRedirect(href(call, update.id))
        }
    }
}

private fun HTML.showAllRaces(call: ApplicationCall) {
    val races = STORE.getState().races.getAll().sortedBy { it.name }
    val count = races.size
    val createLink = call.application.href(Races.New(Races()))

    simpleHtml("Races") {
        field("Count", count.toString())
        showList(races) { race ->
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
            h2 { +"Appearance Options" }
            h3 { +"Skin" }
            selectRaritiesForEnumRarity("Scale Colors", SCALE_COLOR, race.appearance.scalesColors)
            selectRaritiesForEnumRarity(
                "Normal Skin Colors",
                NORMAL_SKIN_COLOR,
                race.appearance.normalSkinColors
            )
            selectRaritiesForEnumRarity(
                "Exotic Skin Colors",
                EXOTIC_SKIN_COLOR,
                race.appearance.exoticSkinColors
            )
            h3 { +"Ears" }
            selectRaritiesForEnumRarity("Ear Shapes", EAR_SHAPE, race.appearance.earShapes)
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

private fun parseRace(id: RaceId, parameters: Parameters): Race {
    val name = parameters.getOrFail("name")
    return Race(id, name, parseAppearanceOptions(parameters))
}

private fun parseAppearanceOptions(parameters: Parameters): AppearanceOptions {
    val scalesColors = parseEnumRarity(parameters, SCALE_COLOR, Color::valueOf)
    val normalSkinColors = parseEnumRarity(parameters, NORMAL_SKIN_COLOR, SkinColor::valueOf)
    val exoticSkinColors = parseEnumRarity(parameters, EXOTIC_SKIN_COLOR, Color::valueOf)
    val earShapes = parseEnumRarity(parameters, EAR_SHAPE, EarShape::valueOf)

    return AppearanceOptions(scalesColors, normalSkinColors, exoticSkinColors, earShapes)
}