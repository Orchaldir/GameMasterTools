package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.StreetAddress
import at.orchaldir.gm.core.model.world.building.TownAddress
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.getHouseNumbersUsedByOthers(town: TownMapId, address: StreetAddress) =
    getUsedHouseNumbers(town, address.street) - address.houseNumber

fun State.getHouseNumbersUsedByOthers(town: TownMapId, address: TownAddress) =
    getUsedHouseNumbers(town) - address.houseNumber

fun State.getUsedHouseNumbers(town: TownMapId, street: StreetId) = getBuildings(town)
    .mapNotNull {
        if (it.address is StreetAddress && it.address.street == street) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

fun State.getUsedHouseNumbers(town: TownMapId) = getBuildings(town)
    .mapNotNull {
        if (it.address is TownAddress) {
            it.address.houseNumber
        } else {
            null
        }
    }
    .toSet()

