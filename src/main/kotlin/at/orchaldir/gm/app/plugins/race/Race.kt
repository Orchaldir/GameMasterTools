package at.orchaldir.gm.app.plugins.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseRace
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStagesType
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getAppearanceForAge
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.visualizeGroup
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

            val state = STORE.getState()
            val race = state.getRaceStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, state, race)
            }
        }
        post<RaceRoutes.Preview> { preview ->
            logger.info { "Get preview for race ${preview.id.value}" }

            val race = parseRace(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showRaceEditor(call, STORE.getState(), race)
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

    simpleHtml("Races") {
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
    val backLink = call.application.href(RaceRoutes())
    val deleteLink = call.application.href(RaceRoutes.Delete(race.id))
    val editLink = call.application.href(RaceRoutes.Edit(race.id))

    simpleHtml("Race: ${race.name}") {
        split({
            field("Id", race.id.value.toString())
            field("Name", race.name)
            showRarityMap("Gender", race.genders)
            showDistribution("Height", race.height, "m")
            showLifeStages(call, state, race)
            h2 { +"Characters" }
            showList(state.getCharacters(race.id)) { character ->
                link(call, state, character)
            }
            action(editLink, "Edit")

            if (state.canDelete(race.id)) {
                action(deleteLink, "Delete")
            }

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
    val raceAppearanceId = race.lifeStages.getRaceAppearance()
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(raceAppearanceId)
    val generator = createGeneratorConfig(state, raceAppearance, gender, CultureId(0))
    val appearance = generator.generate()

    val svg = visualizeGroup(RENDER_CONFIG, race.lifeStages.getAllLifeStages().map {
        getAppearanceForAge(race, appearance, it.maxAge)
    })

    p {
        svg(svg, width)
    }
}

private fun HtmlBlockTag.showLifeStages(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val lifeStages = race.lifeStages

    h2 { +"Life Stages" }

    when (lifeStages) {
        is ImmutableLifeStage -> showAppearance(call, state, lifeStages.appearance)

        is SimpleAging -> {
            showAppearance(call, state, lifeStages.appearance)
            showList(lifeStages.lifeStages) { stage ->
                +stage.name
                ul {
                    li {
                        showMaxAge(stage.maxAge)
                    }
                    li {
                        showRelativeSize(stage.relativeSize)
                    }
                    if (stage.hasBeard) {
                        li {
                            p {
                                b { +"Has Beard" }
                            }
                        }
                    }
                    if (stage.hairColor != null) {
                        li {
                            field("Hair Color", stage.hairColor.name)
                        }
                    }
                }
            }
        }
    }
}

private fun HtmlBlockTag.showAppearance(
    call: ApplicationCall,
    state: State,
    id: RaceAppearanceId,
) {
    field("Appearance") {
        link(call, state, id)
    }
}

private fun HtmlBlockTag.showMaxAge(maxAge: Int) {
    field("Max Age", maxAge.toString())
}

private fun HtmlBlockTag.showRelativeSize(size: Factor) {
    field("Relative Size", size.value.toString())
}

private fun HTML.showRaceEditor(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    val backLink = call.application.href(RaceRoutes.Details(race.id))
    val previewLink = call.application.href(RaceRoutes.Preview(race.id))
    val updateLink = call.application.href(RaceRoutes.Update(race.id))

    simpleHtml("Edit Race: ${race.name}") {
        split({
            field("Id", race.id.value.toString())
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectName(race.name)
                selectRarityMap("Gender", GENDER, race.genders)
                selectDistribution("Height", HEIGHT, race.height, 0.1f, 5.0f, 1.0f, 0.01f, "m", true)
                editLifeStages(state, race)
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            race.genders.getValidValues().forEach { gender ->
                visualizeLifeStages(state, race, gender, 120)
            }
        })
    }
}

private fun FORM.editLifeStages(
    state: State,
    race: Race,
) {
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(race.lifeStages.getRaceAppearance())
    val canHaveBeard = raceAppearance.hairOptions.beardTypes.isAvailable(BeardType.Normal)
    val lifeStages = race.lifeStages

    h2 { +"Life Stages" }

    selectValue("Type", combine(LIFE_STAGE, TYPE), LifeStagesType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = when (lifeStages) {
            is ImmutableLifeStage -> type == LifeStagesType.ImmutableLifeStage
            is SimpleAging -> type == LifeStagesType.SimpleAging
        }
    }

    when (lifeStages) {
        is ImmutableLifeStage -> {
            selectAppearance(state, lifeStages.appearance, 0)
        }

        is SimpleAging -> {
            selectAppearance(state, lifeStages.appearance, 0)
            selectNumberOfLifeStages(lifeStages.lifeStages.size)
            var minMaxAge = 1
            showListWithIndex(lifeStages.lifeStages) { index, stage ->
                selectStageName(index, stage.name)
                ul {
                    li {
                        selectMaxAge(minMaxAge, index, stage.maxAge)
                    }
                    li {
                        selectRelativeSize(stage.relativeSize, index)
                    }
                    li {
                        selectBool(
                            "Has Beard",
                            stage.hasBeard && canHaveBeard,
                            combine(LIFE_STAGE, BEARD, index),
                            !canHaveBeard
                        )
                    }
                    li {
                        selectOptionalColor(
                            "Hair Color",
                            combine(LIFE_STAGE, HAIR_COLOR, index),
                            stage.hairColor,
                            Color.entries,
                            true
                        )
                    }
                }
                minMaxAge = stage.maxAge + 1
            }
        }
    }
}

private fun FORM.selectNumberOfLifeStages(number: Int) {
    selectInt("Weekdays", number, 2, 100, LIFE_STAGE, true)
}

private fun LI.selectStageName(
    index: Int,
    name: String,
) {
    selectText("Name", name, combine(LIFE_STAGE, NAME, index), 1)
}

private fun LI.selectMaxAge(
    minMaxAge: Int,
    index: Int,
    maxAge: Int?,
) {
    selectInt("Max Age", maxAge ?: 0, minMaxAge, 10000, combine(LIFE_STAGE, AGE, index))
}

private fun LI.selectRelativeSize(
    size: Factor,
    index: Int,
) {
    selectFloat("Relative Size", size.value, 0.01f, 1.0f, 0.01f, combine(LIFE_STAGE, SIZE, index), true)
}

private fun HtmlBlockTag.selectAppearance(
    state: State,
    raceAppearanceId: RaceAppearanceId,
    index: Int,
) {
    selectValue(
        "Appearance",
        combine(RACE, APPEARANCE, index),
        state.getRaceAppearanceStorage().getAll(),
        true,
    ) { appearance ->
        label = appearance.name
        value = appearance.id.value.toString()
        selected = appearance.id == raceAppearanceId
    }
}
