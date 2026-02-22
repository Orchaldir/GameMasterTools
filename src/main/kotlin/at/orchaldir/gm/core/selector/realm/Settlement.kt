package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EmployedBySettlement
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getSettlementMaps
import at.orchaldir.gm.utils.Id

fun State.canDeleteSettlement(settlement: SettlementId) = DeleteResult(settlement)
    .addElements(getRealmsWithCapital(settlement))
    .addElements(getRealmsWithPreviousCapital(settlement))
    .addElements(getDistricts(settlement))
    .addElements(getEmployees(settlement))
    .addElements(getPreviousEmployees(settlement))
    .addElements(getSettlementMaps(settlement))
    .addElements(getWarsWithParticipant(settlement))
    .apply { canDeleteCreator(settlement, it) }
    .apply { canDeleteDestroyer(settlement, it) }
    .apply { canDeleteOwner(settlement, it) }
    .apply { canDeleteWithPositions(settlement, it) }

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

        if (employmentStatus is EmployedBySettlement && employmentStatus.job == job) {
            employmentStatus.settlement
        } else {
            null
        }
    }
    .toSet()