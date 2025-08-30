package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.building.TownAddress
import at.orchaldir.gm.core.model.world.street.StreetId

fun getHouseNumbersUsedByOthers(buildings: Collection<Building>, address: StreetAddress) =
    getUsedHouseNumbers(buildings, address.street) - address.houseNumber

fun getHouseNumbersUsedByOthers(buildings: Collection<Building>, address: TownAddress) =
    getUsedHouseNumbers(buildings) - address.houseNumber

fun getUsedHouseNumbers(buildings: Collection<Building>, street: StreetId) = buildings
    .mapNotNull {
        if (it.address is StreetAddress && it.address.street == street) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

fun getUsedHouseNumbers(buildings: Collection<Building>) = buildings
    .mapNotNull {
        if (it.address is TownAddress) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

