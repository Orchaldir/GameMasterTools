package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.editRaceAppearance
import at.orchaldir.gm.app.html.race.parseRaceAppearance
import at.orchaldir.gm.app.html.race.showRaceAppearance
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.generator.AppearanceGeneratorConfig
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.race.appearance.RACE_APPEARANCE_TYPE
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.SortRaceAppearance
import at.orchaldir.gm.core.selector.race.getRaces
import at.orchaldir.gm.core.selector.util.sortRaceAppearances
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@Resource("/$RACE_APPEARANCE_TYPE")
class RaceAppearanceRoutes : Routes<RaceAppearanceId> {
    @Resource("all")
    class All(
        val sort: SortRaceAppearance = SortRaceAppearance.Name,
        val parent: RaceAppearanceRoutes = RaceAppearanceRoutes(),
    )

    @Resource("gallery")
    class Gallery(
        val sort: SortRaceAppearance = SortRaceAppearance.Name,
        val parent: RaceAppearanceRoutes = RaceAppearanceRoutes(),
    )

    @Resource("details")
    class Details(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("new")
    class New(val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("clone")
    class Clone(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("delete")
    class Delete(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("edit")
    class Edit(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("preview")
    class Preview(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    @Resource("update")
    class Update(val id: RaceAppearanceId, val parent: RaceAppearanceRoutes = RaceAppearanceRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun clone(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Edit(id))
}

fun Application.configureRaceAppearanceRouting() {
    routing {
        get<RaceAppearanceRoutes.All> { all ->
            logger.info { "Get all races appearances" }

            call.respondHtml(HttpStatusCode.OK) {
                showAll(call, STORE.getState(), all.sort)
            }
        }
        get<RaceAppearanceRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState(), gallery.sort)
            }
        }
        get<RaceAppearanceRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                RaceAppearanceRoutes(),
                HtmlBlockTag::showRaceAppearance
            ) { _, state, appearance ->
                showRandomExamples(state, appearance, 20, 20)
            }
        }
        get<RaceAppearanceRoutes.New> {
            handleCreateElement(STORE.getState().getRaceAppearanceStorage()) { id ->
                RaceAppearanceRoutes.Edit(id)
            }
        }
        get<RaceAppearanceRoutes.Clone> { clone ->
            handleCloneElement(clone.id) { cloneId ->
                RaceAppearanceRoutes.Edit(cloneId)
            }
        }
        get<RaceAppearanceRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RaceAppearanceRoutes())
        }
        get<RaceAppearanceRoutes.Edit> { edit ->
            logger.info { "Get editor for race appearance ${edit.id.value}" }

            val state = STORE.getState()
            val race = state.getRaceAppearanceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, state, race)
            }
        }
        post<RaceAppearanceRoutes.Preview> { preview ->
            logger.info { "Get preview for race appearance ${preview.id.value}" }

            val state = STORE.getState()
            val race = parseRaceAppearance(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEditor(call, state, race)
            }
        }
        post<RaceAppearanceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRaceAppearance)
        }
    }
}

private fun HTML.showAll(
    call: ApplicationCall,
    state: State,
    sort: SortRaceAppearance,
) {
    val appearances = state.sortRaceAppearances(sort)
    val createLink = call.application.href(RaceAppearanceRoutes.New())
    val galleryLink = call.application.href(RaceAppearanceRoutes.Gallery())

    simpleHtml("Race Appearances") {
        field("Count", appearances.size)
        action(galleryLink, "Gallery")

        table {
            tr {
                th { +"Name" }
                th { +"Races" }
            }
            appearances.forEach { appearance ->
                tr {
                    tdLink(call, state, appearance)
                    tdInlineElements(call, state, state.getRaces(appearance.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
    sort: SortRaceAppearance,
) {
    val appearances = state.sortRaceAppearances(sort)
    val backLink = call.application.href(RaceAppearanceRoutes())

    simpleHtml("Race Appearances") {
        showGallery(call, state, appearances) { element ->
            getSvg(state, element)
        }

        back(backLink)
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
        AppearanceFashion(),
        Gender.Male,
        Distribution.fromMeters(1.0f, ZERO),
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
    val backLink = call.application.href(RaceAppearanceRoutes.Details(appearance.id))
    val previewLink = call.application.href(RaceAppearanceRoutes.Preview(appearance.id))
    val updateLink = call.application.href(RaceAppearanceRoutes.Update(appearance.id))

    simpleHtmlEditor(appearance, true) {
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
        AppearanceFashion(),
        Gender.Male,
        Distribution.fromMeters(1.0f, ZERO),
    )

    return visualizeCharacter(state, CHARACTER_CONFIG, generator.generate())
}

fun createGeneratorConfig(
    state: State,
    appearance: RaceAppearance,
    appearanceFashion: AppearanceFashion,
    gender: Gender,
    height: Distribution<Distance>,
) = AppearanceGeneratorConfig(
    RandomNumberGenerator(Random),
    state.rarityGenerator,
    gender,
    height,
    appearance,
    appearanceFashion,
)