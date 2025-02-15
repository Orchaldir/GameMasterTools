package at.orchaldir.gm.app.html.model.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showRace(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    showRarityMap("Gender", race.genders)
    showDistribution("Height", race.height)
    showLifeStages(call, state, race)
    showRaceOrigin(call, state, race.origin)
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
            details {
                showList(lifeStages.lifeStages, HtmlBlockTag::showLifeStage)
            }
        }
    }
}

private fun HtmlBlockTag.showLifeStage(stage: LifeStage) {
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
                field("Hair Color", stage.hairColor)
            }
        }
    }
}

private fun HtmlBlockTag.showAppearance(
    call: ApplicationCall,
    state: State,
    id: RaceAppearanceId,
) {
    fieldLink("Appearance", call, state, id)
}

private fun HtmlBlockTag.showMaxAge(maxAge: Int) {
    field("Max Age", maxAge)
}

private fun HtmlBlockTag.showRelativeSize(size: Factor) {
    field("Relative Size", size.value.toString())
}

// edit

fun FORM.editRace(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    selectName(race.name)
    selectRarityMap("Gender", GENDER, race.genders)
    selectDistribution(
        "Height",
        HEIGHT,
        race.height,
        Distance(100),
        Distance(5000),
        Distance(1000),
        Distance(10),
        true
    )
    editLifeStages(state, race)
    editRaceOrigin(state, race)
}


private fun FORM.editLifeStages(
    state: State,
    race: Race,
) {
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(race.lifeStages.getRaceAppearance())
    val canHaveBeard = raceAppearance.hairOptions.beardTypes.isAvailable(BeardType.Normal)
    val lifeStages = race.lifeStages

    h2 { +"Life Stages" }

    selectValue("Type", combine(LIFE_STAGE, TYPE), LifeStagesType.entries, lifeStages.getType(), true)

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
    selectInt("Life Stages", number, 2, 100, 1, LIFE_STAGE, true)
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
    selectInt("Max Age", maxAge ?: 0, minMaxAge, 10000, 1, combine(LIFE_STAGE, AGE, index), true)
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
    selectElement(
        state,
        "Appearance",
        combine(RACE, APPEARANCE, index),
        state.getRaceAppearanceStorage().getAll().sortedBy { it.name },
        raceAppearanceId,
        true,
    )
}

// parse

fun parseRaceId(parameters: Parameters, param: String) = RaceId(parseInt(parameters, param))

fun parseRace(state: State, parameters: Parameters, id: RaceId): Race {
    val name = parameters.getOrFail("name")
    return Race(
        id, name,
        parseOneOf(parameters, GENDER, Gender::valueOf),
        parseDistribution(parameters, HEIGHT),
        parseLifeStages(parameters),
        parseRaceOrigin(parameters, state),
    )
}

private fun parseLifeStages(parameters: Parameters): LifeStages {
    return when (parameters[combine(LIFE_STAGE, TYPE)]) {
        LifeStagesType.ImmutableLifeStage.name -> ImmutableLifeStage(
            parseAppearanceId(parameters, 0),
        )

        LifeStagesType.SimpleAging.name -> SimpleAging(
            parseAppearanceId(parameters, 0),
            parseSimpleLifeStages(parameters),
        )

        else -> error("Unsupported")
    }

}

private fun parseSimpleLifeStages(parameters: Parameters): List<LifeStage> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseSimpleLifeStage(parameters, it) }
}

private fun parseSimpleLifeStage(parameters: Parameters, index: Int) = LifeStage(
    parseOptionalString(parameters, combine(LIFE_STAGE, NAME, index)) ?: "${index + 1}.Life Stage",
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), 2),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
    parseBool(parameters, combine(LIFE_STAGE, BEARD, index)),
    parse<Color>(parameters, combine(LIFE_STAGE, HAIR_COLOR, index)),
)

private fun parseAppearanceId(parameters: Parameters, index: Int) =
    parseRaceAppearanceId(parameters, combine(RACE, APPEARANCE, index))
