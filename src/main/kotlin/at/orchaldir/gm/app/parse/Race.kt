package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRace(id: RaceId, parameters: Parameters): Race {
    val name = parameters.getOrFail("name")
    return Race(
        id, name,
        parseOneOf(parameters, GENDER, Gender::valueOf),
    )
}
