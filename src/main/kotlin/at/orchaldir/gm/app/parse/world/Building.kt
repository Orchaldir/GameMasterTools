package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.util.parseBuildingPurpose
import at.orchaldir.gm.app.html.util.parseCreator
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.parseOwnership
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseOptionalName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import io.ktor.http.*

fun parseBuildingId(parameters: Parameters, param: String, default: Int = 0) =
    BuildingId(parseInt(parameters, param, default))

fun parseUpdateBuilding(parameters: Parameters, state: State, id: BuildingId): UpdateBuilding {
    val constructionDate = parseOptionalDate(parameters, state, DATE)

    return UpdateBuilding(
        id,
        parseOptionalName(parameters),
        parseAddress(parameters),
        constructionDate,
        parseOwnership(parameters, state, constructionDate),
        parseOptionalArchitecturalStyleId(parameters, STYLE),
        parseBuildingPurpose(parameters, state),
        parseCreator(parameters),
    )
}

fun parseAddress(parameters: Parameters): Address = when (parameters[combine(ADDRESS, TYPE)]) {
    AddressType.Town.toString() -> TownAddress(parseInt(parameters, combine(ADDRESS, NUMBER), 1))
    AddressType.Street.toString() -> StreetAddress(
        parseStreetId(parameters, combine(ADDRESS, STREET)),
        parseInt(parameters, combine(ADDRESS, NUMBER), 1),
    )

    AddressType.Crossing.toString() -> CrossingAddress(parseStreets(parameters))

    else -> NoAddress
}

private fun parseStreets(parameters: Parameters): List<StreetId> {
    val count = parseInt(parameters, combine(ADDRESS, STREET, NUMBER), 2)

    return (0..<count)
        .map { parseStreetId(parameters, combine(ADDRESS, STREET, it)) }
}
