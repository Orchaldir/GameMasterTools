package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.building.TownAddress
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.getHouseNumbersUsedByOthers(town: TownId, address: StreetAddress) =
    getUsedHouseNumbers(town, address.street) - address.houseNumber

fun State.getHouseNumbersUsedByOthers(town: TownId, address: TownAddress) =
    getUsedHouseNumbers(town) - address.houseNumber

fun State.getUsedHouseNumbers(town: TownId, street: StreetId) = getBuildings(town)
    .mapNotNull {
        if (it.address is StreetAddress && it.address.street == street) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

fun State.getUsedHouseNumbers(town: TownId) = getBuildings(town)
    .mapNotNull {
        if (it.address is TownAddress) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

