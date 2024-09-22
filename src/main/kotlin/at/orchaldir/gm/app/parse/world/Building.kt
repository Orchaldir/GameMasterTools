package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBuildingId(parameters: Parameters, param: String) = BuildingId(parseInt(parameters, param))

fun parseUpdateBuilding(parameters: Parameters, state: State, id: BuildingId): UpdateBuilding {
    val constructionDate = parseDate(parameters, state, DATE)

    return UpdateBuilding(
        id,
        parameters.getOrFail(NAME),
        constructionDate,
        parseOwnership(parameters, state, constructionDate),
    )
}

fun parseOwnership(parameters: Parameters, state: State, startDate: Date): Ownership = Ownership(
    parseOwner(parameters, OWNER),
    parsePreviousOwners(parameters, state, startDate),
)

private fun parsePreviousOwners(parameters: Parameters, state: State, startDate: Date): List<PreviousOwner> {
    val param = combine(OWNER, HISTORY)
    val count = parseInt(parameters, param, 0)
    var minDate = startDate.next()

    return (0..<count)
        .map {
            val previousOwner = parsePreviousOwner(parameters, state, combine(param, it), minDate)
            minDate = previousOwner.until.next()

            previousOwner
        }
}

fun parsePreviousOwner(parameters: Parameters, state: State, param: String, minDate: Date) = PreviousOwner(
    parseOwner(parameters, param),
    parseDate(parameters, state, combine(param, DATE), minDate),
)

fun parseOwner(parameters: Parameters, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UnknownOwner
}
