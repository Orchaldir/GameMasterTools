package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBuildingId(parameters: Parameters, param: String) = BuildingId(parseInt(parameters, param))

fun parseUpdateBuilding(parameters: Parameters, state: State, id: BuildingId) = UpdateBuilding(
    id,
    parameters.getOrFail(NAME),
    parseDate(parameters, state, DATE),
    parseOwnership(parameters, state),
)

fun parseOwnership(parameters: Parameters, state: State): Ownership = Ownership(
    parseOwner(parameters, OWNER),
    parsePreviousOwners(parameters, state),
)

private fun parsePreviousOwners(parameters: Parameters, state: State): List<PreviousOwner> {
    val param = combine(OWNER, HISTORY)
    val count = parseInt(parameters, param, 0)

    return (0..<count)
        .map { parsePreviousOwner(parameters, state, combine(param, it)) }
}

fun parsePreviousOwner(parameters: Parameters, state: State, param: String) = PreviousOwner(
    parseOwner(parameters, param),
    parseDate(parameters, state, combine(param, DATE))
)

fun parseOwner(parameters: Parameters, param: String): Owner = when (parameters[param]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(param, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(param, TOWN)))
    else -> UnknownOwner
}
