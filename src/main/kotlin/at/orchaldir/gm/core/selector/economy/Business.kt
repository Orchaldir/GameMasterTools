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
import at.orchaldir.gm.core.selector.world.getBuilding
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy

fun State.canDelete(id: BusinessId) = getBuilding(id) == null
        && getEmployees(id).isEmpty()
        && getBuildingsBuildBy(id).isEmpty()

fun State.isInOperation(id: BusinessId, date: Date) = isInOperation(getBusinessStorage().getOrThrow(id), date)

fun State.isInOperation(business: Business, date: Date) = getDefaultCalendar()
    .isAfterOrEqual(date, business.startDate)

fun State.getAgeInYears(business: Business) = getDefaultCalendar()
    .getDurationInYears(business.startDate, time.currentDate)

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

fun State.getOpenBusinesses(date: Date) = getBusinessStorage().getAll()
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

// sort

enum class SortBusiness {
    Name,
    Age,
    Employees,
}

fun State.getAgeComparator(): Comparator<Business> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Business, b: Business -> calendar.compareTo(a.startDate, b.startDate) }
}

fun State.sortBusinesses(sort: SortBusiness = SortBusiness.Name) =
    sortBusinesses(getBusinessStorage().getAll(), sort)

fun State.sortBusinesses(businesses: Collection<Business>, sort: SortBusiness = SortBusiness.Name) = businesses
    .sortedWith(when (sort) {
        SortBusiness.Name -> compareBy { it.name }
        SortBusiness.Age -> getAgeComparator()
        SortBusiness.Employees -> compareBy<Business> { getEmployees(it.id).size }.reversed()
    }
    )


