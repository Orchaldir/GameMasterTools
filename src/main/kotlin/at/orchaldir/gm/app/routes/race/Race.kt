package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.editRace
import at.orchaldir.gm.app.html.race.parseRace
import at.orchaldir.gm.app.html.race.showRace
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CloneRace
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.culture.fashion.AppearanceFashion
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.SortRace
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.character.getAppearanceForAge
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.core.selector.util.getTotalPopulation
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.maxOf
import at.orchaldir.gm.visualization.character.appearance.calculatePaddedSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance
import at.orchaldir.gm.visualization.character.appearance.visualizeGroup
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
        get<RaceRoutes.All> { all ->
            logger.info { "Get all races" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRaces(call, STORE.getState(), all.sort)
            }
        }
        get<RaceRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState(), gallery.sort)
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
        get<RaceRoutes.Clone> { clone ->
            logger.info { "Clone race ${clone.id.value}" }

            STORE.dispatch(CloneRace(clone.id))

            call.respondRedirect(call.application.href(RaceRoutes.Edit(STORE.getState().getRaceStorage().lastId)))

            STORE.getState().save()
        }
        get<RaceRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteRace(delete.id), RaceRoutes())
        }
        get<RaceRoutes.Edit> { edit ->
            logger.info { "Get editor for race ${edit.id.value}" }

            val state = STORE.getState()
            val race = state.getRaceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, state, race)
            }
        }
        post<RaceRoutes.Preview> { preview ->
            logger.info { "Get preview for race ${preview.id.value}" }

            val state = STORE.getState()
            val race = parseRace(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, state, race)
            }
        }
        post<RaceRoutes.Update> { update ->
            logger.info { "Update race ${update.id.value}" }

            val race = parseRace(STORE.getState(), call.receiveParameters(), update.id)

            STORE.dispatch(UpdateRace(race))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllRaces(
    call: ApplicationCall,
    state: State,
    sort: SortRace,
) {
    val races = state.sortRaces(sort)
    val createLink = call.application.href(RaceRoutes.New())
    val galleryLink = call.application.href(RaceRoutes.Gallery())

    simpleHtml("Races") {
        action(galleryLink, "Gallery")
        field("Count", races.size)
        showSortLinks(call, RaceRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Gender" }
                th { +"Max Age" }
                th { +"Avg Height" }
                th { +"Avg Weight" }
                th { +"Origin" }
                th { +"Date" }
                th { +"Appearance" }
                th { +"Population" }
                th { +"Characters" }
            }
            races.forEach { race ->
                tr {
                    tdLink(call, state, race)
                    td { +race.genders.getValidValues().joinToString() }
                    tdSkipZero(race.lifeStages.getMaxAge())
                    td { +race.height.center.toString() }
                    td { +race.weight.toString() }
                    td { showOrigin(call, state, race.origin, ::RaceId) }
                    td {
                        title = state.getAgeInYears(race.startDate())?.let { "$it years ago" } ?: ""
                        showOptionalDate(call, state, race.startDate())
                    }
                    tdLink(call, state, race.lifeStages.getRaceAppearance())
                    tdSkipZero(state.getTotalPopulation(race.id))
                    tdSkipZero(state.countCharacters(race.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private inline fun <reified V : Any> HtmlBlockTag.showSortLinks(
    call: ApplicationCall,
    crossinline createLink: Function2<SortRace, RaceRoutes, V>,
) {
    showSortTableLinks(call, SortRace.entries, RaceRoutes(), createLink)
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
    sort: SortRace,
) {
    val races = state.sortRaces(sort)
    val maxHeight = maxOf(races.map { it.height.getMax() })
    val maxSize = CHARACTER_CONFIG.calculateSize(maxHeight)
    val backLink = call.application.href(RaceRoutes.All())

    simpleHtml("Races") {
        showSortLinks(call, RaceRoutes::Gallery)

        showGallery(call, state, races) { race ->
            val lifeStage = race.lifeStages.getAllLifeStages().maxBy { it.relativeSize.toPermyriad() }
            val appearance = generateAppearance(state, race, race.genders.getValidValues().first())
            val appearanceForAge = getAppearanceForAge(race, appearance, lifeStage.maxAge)
            val paddedSize = calculatePaddedSize(CHARACTER_CONFIG, appearanceForAge)

            visualizeAppearance(state, CHARACTER_CONFIG, maxSize, appearanceForAge, paddedSize)
        }

        back(backLink)
    }
}

private fun HTML.showRaceDetails(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val characters = state.getCharacters(race.id)
    val backLink = call.application.href(RaceRoutes.All())
    val cloneLink = call.application.href(RaceRoutes.Clone(race.id))
    val deleteLink = call.application.href(RaceRoutes.Delete(race.id))
    val editLink = call.application.href(RaceRoutes.Edit(race.id))

    simpleHtmlDetails(race) {
        split({
            showRace(call, state, race)

            if (characters.isNotEmpty()) {
                h2 { +"Characters" }

                showList(characters) { character ->
                    link(call, state, character)
                }
            }

            h2 { +"Actions" }

            action(editLink, "Edit")
            action(cloneLink, "Clone")
            action(deleteLink, "Delete")
            back(backLink)
        }, {
            race.genders.getValidValues().forEach { gender ->
                visualizeLifeStages(state, race, gender, 120)
            }
        })
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

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val backLink = call.application.href(RaceRoutes.Details(race.id))
    val previewLink = call.application.href(RaceRoutes.Preview(race.id))
    val updateLink = call.application.href(RaceRoutes.Update(race.id))

    simpleHtmlEditor(race, true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editRace(call, state, race)
            }
        }, {
            race.genders.getValidValues().forEach { gender ->
                visualizeLifeStages(state, race, gender, 120)
            }
        })
    }
}

