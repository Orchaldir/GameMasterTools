package at.orchaldir.gm.app.html.model.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Weight
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
    fieldWeight("Weight", race.weight)
    field("BMI", String.format("%.1f", race.calculateBodyMassIndex()))
    showRaceOrigin(call, state, race.origin)
    showLifeStages(call, state, race)
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

        is DefaultAging -> {
            showAppearance(call, state, lifeStages.appearance)
            showList("Max Ages", lifeStages.maxAges.withIndex().toList()) { indexed ->
                val maxAge = indexed.value
                val stage = DefaultLifeStages.entries[indexed.index].name
                field(stage, "$maxAge years")
            }
            optionalField("Old Age Hair Color", lifeStages.oldAgeHairColor)
            optionalField("Venerable Hair Color", lifeStages.venerableAgeHairColor)
        }

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
            fieldFactor("Relative Size", stage.relativeSize)
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
        fromMillimeters(100),
        fromMeters(5),
        fromMeters(1),
        fromMillimeters(10),
        true
    )
    val kilo = Weight.fromKilogram(1.0f)
    selectWeight("Weight", WEIGHT, race.weight, kilo, Weight.fromKilogram(1000.0f), kilo)
    editRaceOrigin(state, race)
    editLifeStages(state, race)
}


private fun FORM.editLifeStages(
    state: State,
    race: Race,
) {
    val raceAppearance = state.getRaceAppearanceStorage().getOrThrow(race.lifeStages.getRaceAppearance())
    val canHaveBeard = raceAppearance.hair.beardTypes.isAvailable(BeardType.Normal)
    val lifeStages = race.lifeStages

    h2 { +"Life Stages" }

    selectValue("Type", combine(LIFE_STAGE, TYPE), LifeStagesType.entries, lifeStages.getType(), true)

    when (lifeStages) {
        is ImmutableLifeStage -> {
            selectAppearance(state, lifeStages.appearance, 0)
        }

        is DefaultAging -> {
            selectAppearance(state, lifeStages.appearance, 0)
            var minMaxAge = 1
            showListWithIndex(lifeStages.getAllLifeStages()) { index, stage ->
                val nextMaxAge = lifeStages.maxAges.getOrNull(index + 1) ?: 10001
                selectMaxAge(stage.name, index, stage.maxAge, minMaxAge, nextMaxAge - 1)
                minMaxAge = stage.maxAge + 1
            }
            selectHairColor("Old Age Hair Color", 6, lifeStages.oldAgeHairColor)
            selectHairColor("Venerable Hair Color", 7, lifeStages.venerableAgeHairColor)
        }

        is SimpleAging -> {
            selectAppearance(state, lifeStages.appearance, 0)
            selectNumberOfLifeStages(lifeStages.lifeStages.size)
            var minMaxAge = 1
            showListWithIndex(lifeStages.lifeStages) { index, stage ->
                selectStageName(index, stage.name)
                ul {
                    li {
                        val nextMaxAge = lifeStages.lifeStages.getOrNull(index + 1)?.maxAge ?: 10001
                        selectMaxAge("Max Age", index, stage.maxAge, minMaxAge, nextMaxAge - 1)
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
                        selectHairColor("Hair Color", index, stage.hairColor)
                    }
                }
                minMaxAge = stage.maxAge + 1
            }
        }
    }
}

private fun HtmlBlockTag.selectHairColor(label: String, index: Int, color: Color?) {
    selectOptionalColor(
        color,
        combine(LIFE_STAGE, combine(HAIR, COLOR), index),
        label,
        Color.entries,
        true
    )
}

private fun FORM.selectNumberOfLifeStages(number: Int) {
    selectInt("Life Stages", number, 2, 100, 1, LIFE_STAGE, true)
}

private fun HtmlBlockTag.selectStageName(
    index: Int,
    name: String,
) {
    selectText("Name", name, combine(LIFE_STAGE, NAME, index), 1)
}

private fun HtmlBlockTag.selectMaxAge(
    label: String,
    index: Int,
    age: Int,
    minAge: Int,
    maxAge: Int,
) {
    selectInt(label, age, minAge, maxAge, 1, combine(LIFE_STAGE, AGE, index), true)
}

private fun LI.selectRelativeSize(
    size: Factor,
    index: Int,
) {
    selectPercentage("Relative Size", combine(LIFE_STAGE, SIZE, index), size, 1, 100, 1, true)
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

fun parseRace(state: State, parameters: Parameters, id: RaceId) = Race(
    id,
    parameters.getOrFail(NAME),
    parseOneOf(parameters, GENDER, Gender::valueOf),
    parseDistribution(parameters, HEIGHT, ::parseDistance),
    parseWeight(parameters, WEIGHT),
    parseLifeStages(parameters),
    parseRaceOrigin(parameters, state),
)

private fun parseLifeStages(parameters: Parameters): LifeStages {
    return when (parameters[combine(LIFE_STAGE, TYPE)]) {
        LifeStagesType.ImmutableLifeStage.name -> ImmutableLifeStage(
            parseAppearanceId(parameters, 0),
        )

        LifeStagesType.DefaultAging.name -> DefaultAging(
            parseAppearanceId(parameters, 0),
            (0..<DefaultLifeStages.entries.size)
                .map { parseMaxAge(parameters, it) },
            parseHairColor(parameters, 6),
            parseHairColor(parameters, 7),
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
    parseMaxAge(parameters, index),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
    parseBool(parameters, combine(LIFE_STAGE, BEARD, index)),
    parseHairColor(parameters, index),
)

private fun parseMaxAge(parameters: Parameters, index: Int) =
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), 2)

private fun parseHairColor(parameters: Parameters, index: Int) =
    parse<Color>(parameters, combine(LIFE_STAGE, combine(HAIR, COLOR), index))

private fun parseAppearanceId(parameters: Parameters, index: Int) =
    parseRaceAppearanceId(parameters, combine(RACE, APPEARANCE, index))
