package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.EyeShape
import at.orchaldir.gm.core.model.character.appearance.PupilShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.*
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

private const val GENDER = "gender"
private const val SKIN_TYPE = "skin"
private const val SCALE_COLOR = "scale_color"
private const val NORMAL_SKIN_COLOR = "normal_skin_color"
private const val EXOTIC_SKIN_COLOR = "exotic_skin_color"
private const val EARS_LAYOUT = "ears_layout"
private const val EAR_SHAPE = "ear_shape"
private const val EYES_LAYOUT = "eyes_layout"
private const val EYE_SHAPE = "eye_shape"
private const val PUPIL_SHAPE = "pupil_shape"
private const val PUPIL_COLOR = "pupil_color"
private const val SCLERA_COLOR = "sclera_color"
private const val MOUTH_TYPE = "mouth_type"
private const val HAIR_TYPE = "hair"
private const val HAIR_COLOR = "hair_color"
private const val BEARD_TYPE = "beard"

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

            call.respondRedirect(call.application.href(Races.Edit(STORE.getState().races.lastId)))

            STORE.getState().save()
        }
        get<Races.Delete> { delete ->
            logger.info { "Delete race ${delete.id.value}" }

            STORE.dispatch(DeleteRace(delete.id))

            call.respondRedirect(call.application.href(Races()))

            STORE.getState().save()
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

            STORE.getState().save()
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
    val appearance = race.appearance
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(Races())
    val deleteLink = call.application.href(Races.Delete(race.id))
    val editLink = call.application.href(Races.Edit(race.id))

    simpleHtml("Race: ${race.name}") {
        field("Id", race.id.value.toString())
        field("Name", race.name)
        showRarityMap("Gender", race.genders)
        h2 { +"Appearance Options" }
        h3 { +"Skin" }
        showRarityMap("Type", appearance.skinTypes)
        showRarityMap("Scale Colors", appearance.scalesColors)
        showRarityMap("Normal Skin Colors", appearance.normalSkinColors)
        showRarityMap("Exotic Skin Colors", appearance.exoticSkinColors)
        h3 { +"Ears" }
        showRarityMap("Layout", appearance.earsLayout)
        showRarityMap("Ear Shapes", appearance.earShapes)
        h3 { +"Eyes" }
        showRarityMap("Layout", appearance.eyesLayout)
        showRarityMap("Eye Shapes", eyeOptions.eyeShapes)
        showRarityMap("Pupil Shape", eyeOptions.pupilShapes)
        showRarityMap("Pupil Colors", eyeOptions.pupilColors)
        showRarityMap("Sclera Colors", eyeOptions.scleraColors)
        h3 { +"Hair" }
        showRarityMap("Beard", appearance.hairOptions.beardTypes)
        showRarityMap("Hair", appearance.hairOptions.hairTypes)
        showRarityMap("Colors", appearance.hairOptions.colors)
        h3 { +"Mouth" }
        showRarityMap("Types", appearance.mouthTypes)
        h2 { +"Characters" }
        showList(state.getCharacters(race.id)) { character ->
            link(call, character)
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
    race: Race,
) {
    val appearance = race.appearance
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(Races.Details(race.id))
    val updateLink = call.application.href(Races.Update(race.id))

    simpleHtml("Edit Race: ${race.name}") {
        field("Id", race.id.value.toString())
        form {
            field("Name") {
                textInput(name = "name") {
                    value = race.name
                }
            }
            selectRarityMap("Gender", GENDER, race.genders)
            h2 { +"Appearance Options" }
            h3 { +"Skin" }
            selectRarityMap("Type", SKIN_TYPE, appearance.skinTypes)
            selectRarityMap("Scale Colors", SCALE_COLOR, appearance.scalesColors)
            selectRarityMap(
                "Normal Skin Colors",
                NORMAL_SKIN_COLOR,
                appearance.normalSkinColors
            )
            selectRarityMap(
                "Exotic Skin Colors",
                EXOTIC_SKIN_COLOR,
                appearance.exoticSkinColors
            )
            h3 { +"Ears" }
            selectRarityMap("Layout", EARS_LAYOUT, appearance.earsLayout)
            selectRarityMap("Ear Shapes", EAR_SHAPE, appearance.earShapes)
            h3 { +"Eyes" }
            selectRarityMap("Layout", EYES_LAYOUT, appearance.eyesLayout)
            selectRarityMap("Eye Shapes", EYE_SHAPE, eyeOptions.eyeShapes)
            selectRarityMap("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes)
            selectRarityMap("Pupil Colors", PUPIL_COLOR, eyeOptions.pupilColors)
            selectRarityMap("Sclera Colors", SCLERA_COLOR, eyeOptions.scleraColors)
            h3 { +"Hair" }
            selectRarityMap("Beard", BEARD_TYPE, appearance.hairOptions.beardTypes)
            selectRarityMap("Hair", HAIR_TYPE, appearance.hairOptions.hairTypes)
            selectRarityMap("Colors", HAIR_COLOR, appearance.hairOptions.colors)
            h3 { +"Mouth" }
            selectRarityMap("Types", MOUTH_TYPE, appearance.mouthTypes)
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
    return Race(
        id, name,
        parseRarityMap(parameters, GENDER, Gender::valueOf),
        parseAppearanceOptions(parameters)
    )
}

private fun parseAppearanceOptions(parameters: Parameters) = AppearanceOptions(
    parseRarityMap(parameters, SKIN_TYPE, SkinType::valueOf),
    parseRarityMap(parameters, SCALE_COLOR, Color::valueOf),
    parseRarityMap(parameters, NORMAL_SKIN_COLOR, SkinColor::valueOf),
    parseRarityMap(parameters, EXOTIC_SKIN_COLOR, Color::valueOf),
    parseRarityMap(parameters, EARS_LAYOUT, EarsLayout::valueOf),
    parseRarityMap(parameters, EAR_SHAPE, EarShape::valueOf),
    parseRarityMap(parameters, EYES_LAYOUT, EyesLayout::valueOf),
    parseEyeOptions(parameters),
    parseHairOptions(parameters),
    parseRarityMap(parameters, MOUTH_TYPE, MouthType::valueOf),
)

private fun parseEyeOptions(parameters: Parameters): EyeOptions {
    val eyeShapes = parseRarityMap(parameters, EYE_SHAPE, EyeShape::valueOf)
    val pupilShapes = parseRarityMap(parameters, PUPIL_SHAPE, PupilShape::valueOf)
    val pupilColors = parseRarityMap(parameters, PUPIL_COLOR, Color::valueOf)
    val scleraColors = parseRarityMap(parameters, SCLERA_COLOR, Color::valueOf)

    return EyeOptions(eyeShapes, pupilShapes, pupilColors, scleraColors)
}

private fun parseHairOptions(parameters: Parameters) = HairOptions(
    parseRarityMap(parameters, BEARD_TYPE, BeardType::valueOf),
    parseRarityMap(parameters, HAIR_TYPE, HairType::valueOf),
    parseRarityMap(parameters, HAIR_COLOR, Color::valueOf),
)