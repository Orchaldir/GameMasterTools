package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.canDelete(street: StreetId) = getTowns(street).isEmpty()

fun State.getStreets(town: TownId) = getStreetIds(town)
    .map { getStreetStorage().getOrThrow(it) }

fun State.getStreetIds(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getStreet() }.distinct()

fun State.getHouseNumbersUsedByOthers(town: TownId, address: StreetAddress) =
    getUsedHouseNumbers(town, address.street) - address.houseNumber

fun State.getUsedHouseNumbers(town: TownId, street: StreetId) = getBuildings(town)
    .mapNotNull {
        if (it.address is StreetAddress && it.address.street == street) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

