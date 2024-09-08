package at.orchaldir.gm.app.plugins.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.app.plugins.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.EarsLayout
import at.orchaldir.gm.core.model.character.appearance.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getRaces
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

fun Application.configureRaceAppearanceRouting() {
    routing {
        get<AppearanceRoutes> {
            logger.info { "Get all appearances of races" }

            call.respondHtml(HttpStatusCode.OK) {
                showAll(call)
            }
        }
        get<AppearanceRoutes.Details> { details ->
            logger.info { "Get details of race appearance ${details.id.value}" }

            val state = STORE.getState()
            val race = state.getRaceAppearanceStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDetails(call, state, race)
            }
        }
        get<AppearanceRoutes.New> {
            logger.info { "Add new race appearance" }

            STORE.dispatch(CreateRaceAppearance)

            val id = STORE.getState().getRaceAppearanceStorage().lastId
            call.respondRedirect(call.application.href(AppearanceRoutes.Edit(id)))

            STORE.getState().save()
        }
        get<AppearanceRoutes.Delete> { delete ->
            logger.info { "Delete race appearance ${delete.id.value}" }

            STORE.dispatch(DeleteRaceAppearance(delete.id))

            call.respondRedirect(call.application.href(AppearanceRoutes()))

            STORE.getState().save()
        }
        get<AppearanceRoutes.Edit> { edit ->
            logger.info { "Get editor for race appearance ${edit.id.value}" }

            val race = STORE.getState().getRaceAppearanceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, race)
            }
        }
        post<AppearanceRoutes.Preview> { preview ->
            logger.info { "Get preview for race appearance ${preview.id.value}" }

            val race = parseRaceAppearance(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, race)
            }
        }
        post<AppearanceRoutes.Update> { update ->
            logger.info { "Update race appearance ${update.id.value}" }

            val race = parseRaceAppearance(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRaceAppearance(race))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAll(call: ApplicationCall) {
    val elements = STORE.getState().getRaceAppearanceStorage().getAll().sortedBy { it.name }
    val count = elements.size
    val createLink = call.application.href(AppearanceRoutes.New())

    simpleHtml("Race Appearances") {
        field("Count", count.toString())
        showList(elements) { element ->
            link(call, element)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showDetails(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(AppearanceRoutes())
    val deleteLink = call.application.href(AppearanceRoutes.Delete(appearance.id))
    val editLink = call.application.href(AppearanceRoutes.Edit(appearance.id))

    simpleHtml("Race Appearance: ${appearance.name}") {
        field("Id", appearance.id.value.toString())
        field("Name", appearance.name)
        showAppearanceOptions(appearance, eyeOptions)
        h2 { +"Races" }
        showList(state.getRaces(appearance.id)) { race ->
            link(call, race)
        }

        action(editLink, "Edit")

        if (state.canDelete(appearance.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun BODY.showAppearanceOptions(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    showRarityMap("Type", appearance.appearanceTypes)
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

private fun HTML.showEditor(
    call: ApplicationCall,
    appearance: RaceAppearance,
) {
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(AppearanceRoutes.Details(appearance.id))
    val previewLink = call.application.href(AppearanceRoutes.Preview(appearance.id))
    val updateLink = call.application.href(AppearanceRoutes.Update(appearance.id))

    simpleHtml("Edit Race Appearance: ${appearance.name}") {
        field("Id", appearance.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = NAME) {
                    value = appearance.name
                }
            }
            selectRarityMap("Type", APPEARANCE_TYPE, appearance.appearanceTypes)
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
