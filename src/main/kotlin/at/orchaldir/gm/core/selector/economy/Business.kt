package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.world.getBuilding

fun State.canDelete(id: BusinessId) = getBuilding(id) == null

fun State.getAgeInYears(business: Business) = getDefaultCalendar()
    .getDurationInYears(business.startDate, time.currentDate)

fun State.getBusinessesWithBuilding() = getBuildingStorage().getAll()
    .flatMap { it.purpose.getBusinesses() }
    .toSet()

fun State.getBusinessesWithoutBuilding() = getBusinessStorage().getIds() - getBusinessesWithBuilding()

fun State.getBusinesses(job: JobId) = getCharacterStorage().getAll()
    .mapNotNull {
        if (it.employmentStatus is Employed && it.employmentStatus.job == job) {
            it.employmentStatus.business
        } else {
            null
        }
    }
    .toSet()
    .map { getBusinessStorage().getOrThrow(it) }

// owner

fun State.getOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.owner is OwnedByCharacter && it.ownership.owner.character == character }

fun State.getPreviouslyOwnedBusinesses(character: CharacterId) = getBusinessStorage().getAll()
    .filter { it.ownership.contains(character) }

fun State.getOwnedBusinesses(town: TownId) = getBusinessStorage().getAll()
    .filter { it.ownership.owner is OwnedByTown && it.ownership.owner.town == town }

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


