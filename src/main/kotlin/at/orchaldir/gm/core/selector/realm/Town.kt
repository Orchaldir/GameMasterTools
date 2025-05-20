package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.core.selector.world.getTownMaps
import at.orchaldir.gm.utils.Id

fun State.canDeleteTown(town: TownId) = !isCurrentOrFormerOwner(town)
        && !isCreator(town)
        && getTownMaps(town).isEmpty()

// count

fun State.countOwnedTowns(realm: RealmId) = getTownStorage()
    .getAll()
    .count { it.owner.current == realm }

// get

fun State.getExistingTowns(date: Date?) = getExistingElements(getTownStorage().getAll(), date)

fun <ID : Id<ID>> State.countDestroyedTowns(id: ID) = getTownStorage()
    .getAll()
    .count { it.status.isDestroyedBy(id) }

fun <ID : Id<ID>> State.getOwnedTowns(id: ID) = if (id is RealmId) {
    getOwnedTowns(id)
} else {
    emptyList()
}

fun State.getOwnedTowns(realm: RealmId) = getTownStorage()
    .getAll()
    .filter { it.owner.current == realm }

fun <ID : Id<ID>> State.getPreviousOwnedTowns(id: ID) = if (id is RealmId) {
    getPreviousOwnedTowns(id)
} else {
    emptyList()
}

fun State.getPreviousOwnedTowns(realm: RealmId) = getTownStorage()
    .getAll()
    .filter { it.owner.previousEntries.any { it.entry == realm } }

fun State.getTowns(job: JobId) = getCharacterStorage()
    .getAll()
    .mapNotNull {
        val employmentStatus = it.employmentStatus.current

        if (employmentStatus is EmployedByTown && employmentStatus.job == job) {
            employmentStatus.town
        } else {
            null
        }
    }
    .toSet()