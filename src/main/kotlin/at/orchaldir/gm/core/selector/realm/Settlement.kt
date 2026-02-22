package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getSettlementMaps
import at.orchaldir.gm.utils.Id

fun State.canDeleteSettlement(town: SettlementId) = DeleteResult(town)
    .addElements(getRealmsWithCapital(town))
    .addElements(getRealmsWithPreviousCapital(town))
    .addElements(getDistricts(town))
    .addElements(getEmployees(town))
    .addElements(getPreviousEmployees(town))
    .addElements(getSettlementMaps(town))
    .addElements(getWarsWithParticipant(town))
    .apply { canDeleteCreator(town, it) }
    .apply { canDeleteDestroyer(town, it) }
    .apply { canDeleteOwner(town, it) }
    .apply { canDeleteWithPositions(town, it) }

// count

fun State.countOwnedSettlements(realm: RealmId) = getSettlementStorage()
    .getAll()
    .count { it.owner.current == realm }

// get

fun State.getExistingSettlements(date: Date?) = getExistingElements(getSettlementStorage().getAll(), date)

fun <ID : Id<ID>> State.countDestroyedSettlements(id: ID) = getSettlementStorage()
    .getAll()
    .count { it.status.isDestroyedBy(id) }

fun <ID : Id<ID>> State.getOwnedSettlements(id: ID) = if (id is RealmId) {
    getOwnedSettlements(id)
} else {
    emptyList()
}

fun State.getOwnedSettlements(realm: RealmId) = getSettlementStorage()
    .getAll()
    .filter { it.owner.current == realm }

fun <ID : Id<ID>> State.getPreviousOwnedSettlements(id: ID) = if (id is RealmId) {
    getPreviousOwnedSettlements(id)
} else {
    emptyList()
}

fun State.getPreviousOwnedSettlements(realm: RealmId) = getSettlementStorage()
    .getAll()
    .filter { it.owner.previousEntries.any { it.entry == realm } }

fun State.getSettlements(job: JobId) = getCharacterStorage()
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