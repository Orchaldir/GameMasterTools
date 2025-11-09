package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.race.editRace
import at.orchaldir.gm.app.html.race.parseRace
import at.orchaldir.gm.app.html.race.showRace
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.economy.money.CurrencyUnitRoutes.Gallery
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.race.RACE_TYPE
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.model.util.SortRace
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.character.getAppearanceForAge
import at.orchaldir.gm.core.selector.util.getTotalPopulation
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.maxOf
import at.orchaldir.gm.visualization.character.appearance.calculatePaddedSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance
import at.orchaldir.gm.visualization.character.appearance.visualizeGroup
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.p

@Resource("/$RACE_TYPE")
class RaceRoutes : Routes<RaceId, SortRace> {
    @Resource("all")
    class All(
        val sort: SortRace = SortRace.Name,
        val parent: RaceRoutes = RaceRoutes(),
    )

    @Resource("gallery")
    class Gallery(
        val sort: SortRace = SortRace.Name,
        val parent: RaceRoutes = RaceRoutes(),
    )

    @Resource("details")
    class Details(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("new")
    class New(val parent: RaceRoutes = RaceRoutes())

    @Resource("clone")
    class Clone(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("delete")
    class Delete(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("edit")
    class Edit(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("preview")
    class Preview(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("update")
    class Update(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRace) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: RaceId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: RaceId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RaceId) = call.application.href(Edit(id))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun gallery(call: ApplicationCall, sort: SortRace) = call.application.href(Gallery(sort))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RaceId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RaceId) = call.application.href(Update(id))
}

fun Application.configureRaceRouting() {
    routing {
        get<RaceRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RaceRoutes(),
                state.sortRaces(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createOriginColumn(call, state, ::RaceId),
                    tdColumn("Gender") { +it.genders.getValidValues().joinToString() },
                    countColumn("Max Age") { it.lifeStages.getMaxAge() },
                    Column("Avg Height") { td(it.height.center) },
                    Column("Avg Weight") { td(it.weight) },
                    createIdColumn(call, state, "Appearance") { it.lifeStages.getRaceAppearance() },
                    countColumnForId("Population", state::getTotalPopulation),
                    countColumnForId("Characters", state::countCharacters),
                ),
            )
        }
        get<RaceRoutes.Gallery> { gallery ->
            val state = STORE.getState()
            val routes = RaceRoutes()
            val races = state.sortRaces(gallery.sort)
            val maxHeight = maxOf(races.map { it.height.getMax() })
            val maxSize = CHARACTER_CONFIG.calculateSize(maxHeight)

            handleShowGallery(
                state,
                routes,
                races,
                gallery.sort,
            ) { race ->
                val lifeStage = race.lifeStages.getAllLifeStages().maxBy { it.relativeSize.toPermyriad() }
                val appearance = generateAppearance(state, race, race.genders.getValidValues().first())
                val appearanceForAge = getAppearanceForAge(race, appearance, lifeStage.maxAge)
                val paddedSize = calculatePaddedSize(CHARACTER_CONFIG, appearanceForAge)

                visualizeAppearance(state, CHARACTER_CONFIG, maxSize, appearanceForAge, paddedSize)
            }
        }
        get<RaceRoutes.Details> { details ->
            handleShowElementSplit(details.id, RaceRoutes(), HtmlBlockTag::showRace) { _, state, race ->
                race.genders.getValidValues().forEach { gender ->
                    visualizeLifeStages(state, race, gender, 120)
                }
            }
        }
        get<RaceRoutes.New> {
            handleCreateElement(STORE.getState().getRaceStorage()) { id ->
                RaceRoutes.Edit(id)
            }
        }
        get<RaceRoutes.Clone> { clone ->
            handleCloneElement(clone.id) { cloneId ->
                RaceRoutes.Edit(cloneId)
            }
        }
        get<RaceRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RaceRoutes.All())
        }
        get<RaceRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                RaceRoutes(),
                HtmlBlockTag::editRace,
                HtmlBlockTag::showRaceEditorRight,
            )
        }
        post<RaceRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                RaceRoutes(),
                ::parseRace,
                HtmlBlockTag::editRace,
                HtmlBlockTag::showRaceEditorRight,
            )
        }
        post<RaceRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRace)
        }
    }
}

private fun HtmlBlockTag.visualizeLifeStages(
    state: State,
    race: Race,
    gender: Gender,
    width: Int,
) {
    val appearance = generateAppearance(state, race, gender)

    val svg = visualizeGroup(state, CHARACTER_CONFIG, race.lifeStages.getAllLifeStages().map {
        getAppearanceForAge(race, appearance, it.maxAge)
    })

    p {
        svg(svg, width)
    }
}

private fun generateAppearance(
    state: State,
    race: Race,
    gender: Gender,
): Appearance {
    val raceAppearanceId = race.lifeStages.getRaceAppearance()
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(raceAppearanceId)
    val generator = createGeneratorConfig(
        state,
        raceAppearance,
        AppearanceFashion(),
        gender,
        race.height,
    )

    return generator.generate()
}

private fun HtmlBlockTag.showRaceEditorRight(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    race.genders.getValidValues().forEach { gender ->
        visualizeLifeStages(state, race, gender, 120)
    }
}

