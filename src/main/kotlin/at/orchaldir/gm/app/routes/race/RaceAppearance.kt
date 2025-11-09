package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.race.editRaceAppearance
import at.orchaldir.gm.app.html.race.parseRaceAppearance
import at.orchaldir.gm.app.html.race.showRaceAppearance
import at.orchaldir.gm.app.html.svg
import at.orchaldir.gm.app.html.tdInlineElements
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
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import kotlin.random.Random

@Resource("/$RACE_APPEARANCE_TYPE")
class RaceAppearanceRoutes : Routes<RaceAppearanceId, SortRaceAppearance> {
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
    override fun all(call: ApplicationCall, sort: SortRaceAppearance) = call.application.href(All(sort))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun gallery(call: ApplicationCall, sort: SortRaceAppearance) = call.application.href(Gallery(sort))
    override fun clone(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RaceAppearanceId) = call.application.href(Update(id))
}

fun Application.configureRaceAppearanceRouting() {
    routing {
        get<RaceAppearanceRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RaceAppearanceRoutes(),
                state.sortRaceAppearances(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Races") { tdInlineElements(call, state, state.getRaces(it.id)) }
                ),
            )
        }
        get<RaceAppearanceRoutes.Gallery> { gallery ->
            val state = STORE.getState()
            val routes = RaceAppearanceRoutes()
            val units = state.sortRaceAppearances(gallery.sort)

            handleShowGallery(
                state,
                routes,
                units,
                gallery.sort,
            ) { element ->
                getSvg(state, element)
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
            handleCreateElement(RaceAppearanceRoutes(), STORE.getState().getRaceAppearanceStorage())
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
            handleEditElementSplit(
                edit.id,
                RaceAppearanceRoutes(),
                HtmlBlockTag::editRaceAppearance,
                HtmlBlockTag::showRaceAppearanceEditorRight,
            )
        }
        post<RaceAppearanceRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                RaceAppearanceRoutes(),
                ::parseRaceAppearance,
                HtmlBlockTag::editRaceAppearance,
                HtmlBlockTag::showRaceAppearanceEditorRight,
            )
        }
        post<RaceAppearanceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRaceAppearance)
        }
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

private fun HtmlBlockTag.showRaceAppearanceEditorRight(
    call: ApplicationCall,
    state: State,
    appearance: RaceAppearance,
) {
    showRandomExamples(state, appearance, 20, 20)
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