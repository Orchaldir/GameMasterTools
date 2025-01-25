package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.contains
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.world.getBuilding
import at.orchaldir.gm.utils.Id

fun State.canDelete(id: BusinessId) = getBuilding(id) == null
        && getEmployees(id).isEmpty()
        && !isCreator(id)

fun State.isInOperation(id: BusinessId, date: Date?): Boolean {
    val business = getBusinessStorage().getOrThrow(id)

    return isInOperation(business, date)
}

fun State.isInOperation(business: Business, date: Date?) = getDefaultCalendar()
    .isAfterOrEqualOptional(date, business.startDate())

fun State.getBusinessesWithBuilding() = getBuildingStorage().getAll()
    .flatMap { it.purpose.getBusinesses() }
    .toSet()

fun State.getBusinessesWithoutBuilding() = getBusinessStorage().getIds() - getBusinessesWithBuilding()

fun State.getBusinesses(job: JobId) = getCharacterStorage().getAll()
    .mapNotNull {
        val employmentStatus = it.employmentStatus.current

        if (employmentStatus is Employed && employmentStatus.job == job) {
            employmentStatus.business
        } else {
            null
        }
    }
    .toSet()
    .map { getBusinessStorage().getOrThrow(it) }

fun State.getOpenBusinesses(date: Date?) = getBusinessStorage()
    .getAll()
    .filter { isInOperation(it, date) }

// owner

fun State.getOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.current is OwnedByCharacter && it.ownership.current.character == character }

fun State.getPreviouslyOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.contains(character) }

fun State.getOwnedBusinesses(town: TownId) = getBusinessStorage().getAll()
    .filter { it.ownership.current is OwnedByTown && it.ownership.current.town == town }

fun State.getPreviouslyOwnedBusinesses(town: TownId) = getBusinessStorage().getAll()
    .filter { it.ownership.contains(town) }

// founder

fun <ID : Id<ID>> State.getBusinessesFoundedBy(id: ID) = getBusinessStorage().getAll()
    .filter { it.founder.isId(id) }
