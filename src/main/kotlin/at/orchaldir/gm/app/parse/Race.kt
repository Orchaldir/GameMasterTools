package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRace(id: RaceId, parameters: Parameters): Race {
    val name = parameters.getOrFail("name")
    return Race(
        id, name,
        parseOneOf(parameters, GENDER, Gender::valueOf),
        parseLifeStages(parameters),
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

        LifeStagesType.ComplexAging.name -> ComplexAging(
            parseComplexLifeStages(parameters),
        )

        else -> error("Unsupported")
    }

}

private fun parseSimpleLifeStages(parameters: Parameters): List<SimpleLifeStage> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseSimpleLifeStage(parameters, it) }
}

private fun parseSimpleLifeStage(parameters: Parameters, index: Int) = SimpleLifeStage(
    parseName(parameters, combine(LIFE_STAGE, NAME, index)) ?: "${index + 1}.Life Stage",
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), 2),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
)

private fun parseComplexLifeStages(parameters: Parameters): List<ComplexLifeStage> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseComplexLifeStage(parameters, it) }
}

private fun parseComplexLifeStage(parameters: Parameters, index: Int) = ComplexLifeStage(
    parseName(parameters, combine(LIFE_STAGE, NAME, index)) ?: "${index + 1}.Life Stage",
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), 2),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
    parseAppearanceId(parameters, index),
)

private fun parseAppearanceId(parameters: Parameters, index: Int) =
    parseRaceAppearanceId(parameters, combine(RACE, APPEARANCE, index))
