package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.world.getBuilding

fun State.canDelete(id: BusinessId) = getBuilding(id) == null

fun State.getAgeInYears(business: Business) = getDefaultCalendar()
    .getDurationInYears(business.startDate, time.currentDate)

fun State.getBusinessesWithBuilding() = getBuildingStorage().getAll()
    .flatMap { it.purpose.getBusinesses() }
    .toSet()

fun State.getBusinessesWithoutBuilding() = getBusinessStorage().getIds() - getBusinessesWithBuilding()

fun State.getOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.owner is OwnedByCharacter && it.ownership.owner.character == character }

fun State.getPreviouslyOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.contains(character) }



