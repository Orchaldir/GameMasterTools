package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.building.TownAddress
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.utils.Id

fun <ID : Id<ID>> State.getHouseNumbersUsedByOthers(id: ID, address: StreetAddress) =
    getUsedHouseNumbers(id, address.street) - address.houseNumber

fun <ID : Id<ID>> State.getHouseNumbersUsedByOthers(id: ID, address: TownAddress) =
    getUsedHouseNumbers(id) - address.houseNumber

fun <ID : Id<ID>> State.getUsedHouseNumbers(id: ID, street: StreetId) = getBuildings(id)
    .mapNotNull {
        if (it.address is StreetAddress && it.address.street == street) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

fun <ID : Id<ID>> State.getUsedHouseNumbers(id: ID) = getBuildings(id)
    .mapNotNull {
        if (it.address is TownAddress) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

