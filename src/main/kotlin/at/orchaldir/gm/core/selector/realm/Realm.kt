package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.utils.Id

fun State.canDeleteRealm(realm: RealmId) = !isCreator(realm)
        && !isCurrentOrFormerOwner(realm)
        && countWars(realm) == 0
        && getSubRealms(realm).isEmpty()
        && getPreviousSubRealms(realm).isEmpty()
        && getOwnedTowns(realm).isEmpty()
        && getPreviousOwnedTowns(realm).isEmpty()

// count

fun State.countRealmsDestroyedByCatastrophe(catastrophe: CatastropheId) = getRealmStorage()
    .getAll()
    .count { it.status.isDestroyedByCatastrophe(catastrophe) }

fun State.countRealmsDestroyedByWar(war: WarId) = getRealmStorage()
    .getAll()
    .count { it.status.isDestroyedByWar(war) }

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

fun State.getRealmsDestroyedByCatastrophe(catastrophe: CatastropheId) = getRealmStorage()
    .getAll()
    .filter { it.status.isDestroyedByCatastrophe(catastrophe) }

fun State.getRealmsDestroyedByWar(war: WarId) = getRealmStorage()
    .getAll()
    .filter { it.status.isDestroyedByWar(war) }
