package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.app.parse.parseDate
import at.orchaldir.gm.app.parse.parseInt
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
    parseOwner(parameters),
)

fun parseOwner(parameters: Parameters): Owner = when (parameters[OWNER]) {
    OwnerType.None.toString() -> NoOwner
    OwnerType.Character.toString() -> OwnedByCharacter(parseCharacterId(parameters, combine(OWNER, CHARACTER)))
    OwnerType.Town.toString() -> OwnedByTown(parseTownId(parameters, combine(OWNER, TOWN)))
    else -> UnknownOwner
}
