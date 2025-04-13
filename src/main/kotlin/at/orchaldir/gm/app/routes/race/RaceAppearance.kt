package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.race.editRaceAppearance
import at.orchaldir.gm.app.html.model.race.parseRaceAppearance
import at.orchaldir.gm.app.html.model.race.showRaceAppearance
import at.orchaldir.gm.app.routes.race.RaceRoutes.AppearanceRoutes
import at.orchaldir.gm.core.action.CloneRaceAppearance
import at.orchaldir.gm.core.action.CreateRaceAppearance
import at.orchaldir.gm.core.action.DeleteRaceAppearance
import at.orchaldir.gm.core.action.UpdateRaceAppearance
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.fashion.AppearanceStyle
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.culture.canDelete
import at.orchaldir.gm.core.selector.getRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.renderer.svg.Svg
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
        get<AppearanceRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
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
    val elements = STORE.getState().getRaceAppearanceStorage()
        .getAll()
        .sortedBy { it.name }
    val createLink = call.application.href(AppearanceRoutes.New())
    val galleryLink = call.application.href(AppearanceRoutes.Gallery())

    simpleHtml("Race Appearances") {
        field("Count", elements.size)
        action(galleryLink, "Gallery")

        showList(elements) { element ->
            link(call, element)
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val elements = STORE.getState().getRaceAppearanceStorage()
        .getAll()
        .sortedBy { it.name }
    val backLink = call.application.href(AppearanceRoutes())

    simpleHtml("Race Appearances") {
        showGallery(call, state, elements) { element ->
            getSvg(state, element)
        }

        back(backLink)
    }
}

private fun HTML.showDetails(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    val eyeOptions = appearance.eye
    val backLink = call.application.href(AppearanceRoutes())
    val cloneLink = call.application.href(AppearanceRoutes.Clone(appearance.id))
    val deleteLink = call.application.href(AppearanceRoutes.Delete(appearance.id))
    val editLink = call.application.href(AppearanceRoutes.Edit(appearance.id))

    simpleHtml("Race Appearance: ${appearance.name}") {
        split({
            field("Name", appearance.name)
            h2 { +"Options" }

            showRaceAppearance(call, state, appearance, eyeOptions)

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
    val generator = createGeneratorConfig(
        state,
        appearance,
        AppearanceStyle(),
        Gender.Male,
        Distribution.fromMeters(1.0f, 0.0f),
    )

    repeat(n) {
        val svg = visualizeCharacter(state, CHARACTER_CONFIG, generator.generate())
        svg(svg, width)
    }
}

private fun HTML.showEditor(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    val backLink = call.application.href(AppearanceRoutes.Details(appearance.id))
    val previewLink = call.application.href(AppearanceRoutes.Preview(appearance.id))
    val updateLink = call.application.href(AppearanceRoutes.Update(appearance.id))

    simpleHtml("Edit Race Appearance: ${appearance.name}", true) {

        split({
            formWithPreview(previewLink, updateLink, backLink) {
                selectName(appearance.name)

                h2 { +"Options" }

                editRaceAppearance(state, appearance, appearance.eye)
            }
        }, {
            showRandomExamples(state, appearance, 20, 20)
        })
    }
}

private fun getSvg(
    state: State,
    appearance: RaceAppearance,
): Svg {
    val generator = createGeneratorConfig(
        state,
        appearance,
        AppearanceStyle(),
        Gender.Male,
        Distribution.fromMeters(1.0f, 0.0f),
    )

    return visualizeCharacter(state, CHARACTER_CONFIG, generator.generate())
}

fun createGeneratorConfig(
    state: State,
    appearance: RaceAppearance,
    appearanceStyle: AppearanceStyle,
    gender: Gender,
    height: Distribution<Distance>,
) = AppearanceGeneratorConfig(
    RandomNumberGenerator(Random),
    state.rarityGenerator,
    gender,
    height,
    appearance,
    appearanceStyle,
)