package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseRaceAppearance
import at.orchaldir.gm.app.routes.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.core.action.CloneRaceAppearance
import at.orchaldir.gm.core.action.CreateRaceAppearance
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.EarsLayout
import at.orchaldir.gm.core.model.character.appearance.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.math.Distribution
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
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
import kotlin.random.Random

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
        get<AppearanceRoutes.Clone> { clone ->
            logger.info { "Clone race appearance ${clone.id.value}" }

            STORE.dispatch(CloneRaceAppearance(clone.id))

            call.respondRedirect(
                call.application.href(
                    AppearanceRoutes.Edit(
                        STORE.getState().getRaceAppearanceStorage().lastId
                    )
                )
            )

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

            val state = STORE.getState()
            val race = state.getRaceAppearanceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, state, race)
            }
        }
        post<AppearanceRoutes.Preview> { preview ->
            logger.info { "Get preview for race appearance ${preview.id.value}" }

            val race = parseRaceAppearance(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, STORE.getState(), race)
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
    val createLink = call.application.href(AppearanceRoutes.New())

    simpleHtml("Race Appearances") {
        field("Count", elements.size)
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
    val cloneLink = call.application.href(AppearanceRoutes.Clone(appearance.id))
    val deleteLink = call.application.href(AppearanceRoutes.Delete(appearance.id))
    val editLink = call.application.href(AppearanceRoutes.Edit(appearance.id))

    simpleHtml("Race Appearance: ${appearance.name}") {
        split({
            field("Name", appearance.name)
            h2 { +"Options" }
            showAppearanceOptions(appearance, eyeOptions)
            h2 { +"Races" }
            showList(state.getRaces(appearance.id)) { race ->
                link(call, race)
            }

            action(editLink, "Edit")
            action(cloneLink, "Clone")

            if (state.canDelete(appearance.id)) {
                action(deleteLink, "Delete")
            }

            back(backLink)
        }, {
            showRandomExamples(state, appearance, 20, 20)
        })
    }
}

private fun HtmlBlockTag.showRandomExamples(
    state: State,
    appearance: RaceAppearance,
    n: Int,
    width: Int,
) {
    val generator = createGeneratorConfig(state, appearance, AppearanceStyle(), Gender.Male)

    repeat(n) {
        val svg = visualizeCharacter(CHARACTER_CONFIG, generator.generate())
        svg(svg, width)
    }
}

private fun HtmlBlockTag.showAppearanceOptions(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    showRarityMap("Type", appearance.appearanceTypes)

    h3 { +"Skin" }

    showRarityMap("Type", appearance.skinTypes)

    if (appearance.skinTypes.isAvailable(SkinType.Fur)) {
        showRarityMap("Fur Colors", appearance.furColors)
    }

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
    state: State,
    appearance: RaceAppearance,
) {
    val eyeOptions = appearance.eyeOptions
    val backLink = call.application.href(AppearanceRoutes.Details(appearance.id))
    val previewLink = call.application.href(AppearanceRoutes.Preview(appearance.id))
    val updateLink = call.application.href(AppearanceRoutes.Update(appearance.id))

    simpleHtml("Edit Race Appearance: ${appearance.name}", true) {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post

                selectName(appearance.name)

                h2 { +"Options" }

                editAppearanceOptions(appearance, eyeOptions)

                button("Update", updateLink)
            }
            back(backLink)
        }, {
            showRandomExamples(state, appearance, 20, 20)
        })
    }
}

private fun FORM.editAppearanceOptions(
    appearance: RaceAppearance,
    eyeOptions: EyeOptions,
) {
    selectRarityMap("Type", APPEARANCE, appearance.appearanceTypes, true)

    h3 { +"Skin" }

    selectRarityMap("Type", SKIN_TYPE, appearance.skinTypes, true)

    if (appearance.skinTypes.isAvailable(SkinType.Fur)) {
        selectRarityMap("Fur Colors", FUR_COLOR, appearance.furColors, true)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Scales)) {
        selectRarityMap("Scale Colors", SCALE_COLOR, appearance.scalesColors, true)
    }

    if (appearance.skinTypes.isAvailable(SkinType.Normal)) {
        selectRarityMap(
            "Normal Skin Colors",
            NORMAL_SKIN_COLOR,
            appearance.normalSkinColors,
            true,
        )
    }

    if (appearance.skinTypes.isAvailable(SkinType.Exotic)) {
        selectRarityMap(
            "Exotic Skin Colors",
            EXOTIC_SKIN_COLOR,
            appearance.exoticSkinColors,
            true,
        )
    }

    h3 { +"Ears" }
    selectRarityMap("Layout", combine(EARS, LAYOUT), appearance.earsLayout, true)
    if (appearance.earsLayout.isAvailable(EarsLayout.NormalEars)) {
        selectRarityMap("Ear Shapes", EAR_SHAPE, appearance.earShapes, true)
    }
    h3 { +"Eyes" }
    selectRarityMap("Layout", combine(EYES, LAYOUT), appearance.eyesLayout, true)
    if (!appearance.eyesLayout.isAvailable(EyesLayout.NoEyes)) {
        selectRarityMap("Eye Shapes", EYE_SHAPE, eyeOptions.eyeShapes, true)
        selectRarityMap("Pupil Shape", PUPIL_SHAPE, eyeOptions.pupilShapes, true)
        selectRarityMap("Pupil Colors", PUPIL_COLOR, eyeOptions.pupilColors, true)
        selectRarityMap("Sclera Colors", SCLERA_COLOR, eyeOptions.scleraColors, true)
    }
    h3 { +"Hair" }
    selectRarityMap("Beard", BEARD, appearance.hairOptions.beardTypes, true)
    selectRarityMap("Hair", HAIR_TYPE, appearance.hairOptions.hairTypes, true)
    if (requiresHairColor(appearance)) {
        selectRarityMap("Colors", HAIR_COLOR, appearance.hairOptions.colors, true)
    }
    h3 { +"Mouth" }
    selectRarityMap("Types", MOUTH_TYPE, appearance.mouthTypes, true)
}

private fun requiresHairColor(appearance: RaceAppearance) =
    appearance.hairOptions.beardTypes.isAvailable(BeardType.Normal) ||
            appearance.hairOptions.hairTypes.isAvailable(HairType.Normal)

fun createGeneratorConfig(
    state: State,
    appearance: RaceAppearance,
    appearanceStyle: AppearanceStyle,
    gender: Gender,
) = AppearanceGeneratorConfig(
    RandomNumberGenerator(Random),
    state.rarityGenerator,
    gender,
    Distribution.fromMeters(1.0f, 0.0f),
    appearance,
    appearanceStyle,
)