package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EmployedByRealm
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.utils.Id

fun State.canDeleteRealm(realm: RealmId) = !isCreator(realm)
        && !isCurrentOrFormerOwner(realm)
        && countWars(realm) == 0
        && countBattles(realm) == 0
        && getSubRealms(realm).isEmpty()
        && getPreviousSubRealms(realm).isEmpty()
        && getOwnedTowns(realm).isEmpty()
        && getPreviousOwnedTowns(realm).isEmpty()

// count

fun <ID : Id<ID>> State.countDestroyedRealms(id: ID) = getRealmStorage()
    .getAll()
    .count { it.status.isDestroyedBy(id) }

fun State.countRealmsWithCurrencyAtAnyTime(currency: CurrencyId) = getRealmStorage()
    .getAll()
    .count { it.currency.current == currency || it.currency.previousEntries.any { it.entry == currency } }

fun State.countRealmsWithLegalCodeAtAnyTime(code: LegalCodeId) = getRealmStorage()
    .getAll()
    .count { it.legalCode.current == code || it.legalCode.previousEntries.any { it.entry == code } }

// get

fun State.getExistingRealms(date: Date?) = getExistingElements(getRealmStorage().getAll(), date)

fun State.getRealmsWithLegalCode(code: LegalCodeId) = getRealmStorage()
    .getAll()
    .filter { it.legalCode.current == code }

fun State.getRealmsWithPreviousLegalCode(code: LegalCodeId) = getRealmStorage()
    .getAll()
    .filter { it.legalCode.previousEntries.any { it.entry == code } }

fun State.getRealmsWithCurrency(code: CurrencyId) = getRealmStorage()
    .getAll()
    .filter { it.currency.current == code }

fun State.getRealmsWithPreviousCurrency(code: CurrencyId) = getRealmStorage()
    .getAll()
    .filter { it.currency.previousEntries.any { it.entry == code } }

fun State.getRealmsWithCapital(town: TownId) = getRealmStorage()
    .getAll()
    .filter { it.capital.current == town }

fun State.getRealmsWithPreviousCapital(town: TownId) = getRealmStorage()
    .getAll()
    .filter { it.capital.previousEntries.any { it.entry == town } }

fun <ID : Id<ID>> State.getSubRealms(id: ID) = if (id is RealmId) {
    getSubRealms(id)
} else {
    emptyList()
}

fun State.getSubRealms(realm: RealmId) = getRealmStorage()
    .getAll()
    .filter { it.owner.current == realm }

fun <ID : Id<ID>> State.getPreviousSubRealms(id: ID) = if (id is RealmId) {
    getPreviousSubRealms(id)
} else {
    emptyList()
}

fun State.getPreviousSubRealms(realm: RealmId) = getRealmStorage()
    .getAll()
    .filter { it.owner.previousEntries.any { it.entry == realm } }

fun State.getRealms(job: JobId) = getCharacterStorage()
    .getAll()
    .mapNotNull {
        val employmentStatus = it.employmentStatus.current

        if (employmentStatus is EmployedByRealm && employmentStatus.job == job) {
            employmentStatus.realm
        } else {
            null
        }
    }
    .toSet()