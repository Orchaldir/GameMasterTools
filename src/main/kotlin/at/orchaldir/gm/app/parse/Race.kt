package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRace(id: RaceId, parameters: Parameters): Race {
    val name = parameters.getOrFail("name")
    return Race(
        id, name,
        parseOneOf(parameters, GENDER, Gender::valueOf),
        parseDistribution(parameters, HEIGHT),
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

        else -> error("Unsupported")
    }

}

private fun parseSimpleLifeStages(parameters: Parameters): List<LifeStage> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseSimpleLifeStage(parameters, it) }
}

private fun parseSimpleLifeStage(parameters: Parameters, index: Int) = LifeStage(
    parseName(parameters, combine(LIFE_STAGE, NAME, index)) ?: "${index + 1}.Life Stage",
    parseInt(parameters, combine(LIFE_STAGE, AGE, index), 2),
    parseFactor(parameters, combine(LIFE_STAGE, SIZE, index)),
    parseBool(parameters, combine(LIFE_STAGE, BEARD, index)),
    parse<Color>(parameters, combine(LIFE_STAGE, HAIR_COLOR, index)),
)

private fun parseAppearanceId(parameters: Parameters, index: Int) =
    parseRaceAppearanceId(parameters, combine(RACE, APPEARANCE, index))
