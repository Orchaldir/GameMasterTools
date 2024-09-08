package at.orchaldir.gm.app.plugins.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.EarsLayout
import at.orchaldir.gm.core.model.character.appearance.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
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

    simpleHtml("RaceRoutes") {
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
    val appearance = race.appearance
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(RaceRoutes())
    val deleteLink = call.application.href(RaceRoutes.Delete(race.id))
    val editLink = call.application.href(RaceRoutes.Edit(race.id))

    simpleHtml("Race: ${race.name}") {
        field("Id", race.id.value.toString())
        field("Name", race.name)
        showRarityMap("Gender", race.genders)
        showAppearanceOptions(appearance, eyeOptions)
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

private fun BODY.showAppearanceOptions(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    h2 { +"Appearance Options" }
    showRarityMap("Type", appearance.appearanceType)
    h3 { +"Skin" }
    showRarityMap("Type", appearance.skinTypes)
    if (appearance.skinTypes.isAvailable(SkinType.Scales)) {
        showRarityMap("Scale Colors", appearance.scalesColors)
    }
    if (appearance.skinTypes.isAvailable(SkinType.Normal)) {
        showRarityMap("Normal Skin Colors", appearance.normalSkinColors)
    }
    if (appearance.skinTypes.isAvailable(SkinType.Exotic)) {
        showRarityMap("Exotic Skin Colors", appearance.exoticSkinColors)
    }
    h3 { +"Ears" }
    showRarityMap("Layout", appearance.earsLayout)
    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        showRarityMap("Ear Shapes", appearance.earShapes)
    }
    h3 { +"Eyes" }
    showRarityMap("Layout", appearance.eyesLayout)
    if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
        showRarityMap("Eye Shapes", eyeOptions.eyeShapes)
        showRarityMap("Pupil Shape", eyeOptions.pupilShapes)
        showRarityMap("Pupil Colors", eyeOptions.pupilColors)
        showRarityMap("Sclera Colors", eyeOptions.scleraColors)
    }
    h3 { +"Hair" }
    showRarityMap("Beard", appearance.hairOptions.beardTypes)
    showRarityMap("Hair", appearance.hairOptions.hairTypes)
    if (requiresHairColor(appearance)) {
        showRarityMap("Colors", appearance.hairOptions.colors)
    }
    h3 { +"Mouth" }
    showRarityMap("Types", appearance.mouthTypes)
}

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    race: Race,
) {
    val appearance = race.appearance
    val eyeOptions = appearance.eyeOptions
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
            h2 { +"Appearance Options" }
            selectRarityMap("Type", APPEARANCE_TYPE, appearance.appearanceType)
            h3 { +"Skin" }
            selectRarityMap("Type", SKIN_TYPE, appearance.skinTypes, true)
            if (appearance.skinTypes.isAvailable(SkinType.Scales)) {
                selectRarityMap("Scale Colors", SCALE_COLOR, appearance.scalesColors)
            }
            if (appearance.skinTypes.isAvailable(SkinType.Normal)) {
                selectRarityMap(
                    "Normal Skin Colors",
                    NORMAL_SKIN_COLOR,
                    appearance.normalSkinColors
                )
            }
            if (appearance.skinTypes.isAvailable(SkinType.Exotic)) {
                selectRarityMap(
                    "Exotic Skin Colors",
                    EXOTIC_SKIN_COLOR,
                    appearance.exoticSkinColors
                )
            }
            h3 { +"Ears" }
            selectRarityMap("Layout", EARS_LAYOUT, appearance.earsLayout, true)
            if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
                selectRarityMap("Ear Shapes", EAR_SHAPE, appearance.earShapes)
            }
            h3 { +"Eyes" }
            selectRarityMap("Layout", EYES_LAYOUT, appearance.eyesLayout, true)
            if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
                selectRarityMap("Eye Shapes", EYE_SHAPE, eyeOptions.eyeShapes)
                selectRarityMap("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes)
                selectRarityMap("Pupil Colors", PUPIL_COLOR, eyeOptions.pupilColors)
                selectRarityMap("Sclera Colors", SCLERA_COLOR, eyeOptions.scleraColors)
            }
            h3 { +"Hair" }
            selectRarityMap("Beard", BEARD_TYPE, appearance.hairOptions.beardTypes, true)
            selectRarityMap("Hair", HAIR_TYPE, appearance.hairOptions.hairTypes, true)
            if (requiresHairColor(appearance)) {
                selectRarityMap("Colors", HAIR_COLOR, appearance.hairOptions.colors)
            }
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
        back(backLink)
    }
}

private fun requiresHairColor(appearance: RaceAppearance) =
    appearance.hairOptions.beardTypes.isAvailable(BeardType.Normal) ||
            appearance.hairOptions.hairTypes.isAvailable(HairType.Normal)
