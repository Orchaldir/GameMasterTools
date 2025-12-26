package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.canDeleteDestroyer
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.world.getRegionsCreatedBy
import at.orchaldir.gm.utils.Id

fun State.canDeleteWar(war: WarId) = DeleteResult(war)
    .addElements(getBattles(war))
    .addElements(getHolidays(war))
    .addElements(getRegionsCreatedBy(war))
    .apply { canDeleteDestroyer(war, it) }

fun <ID : Id<ID>> State.getWarsWithParticipant(id: ID) = getWarStorage()
    .getAll()
    .filter { it.participants.any { participant -> participant.reference.isId(id) } }

fun State.getExistingWars(date: Date?) = getExistingElements(getWarStorage().getAll(), date)

fun State.getWarsEndedBy(catastrophe: CatastropheId) = getWarStorage()
    .getAll()
    .filter { it.status.isEndedBy(catastrophe) }

fun State.getWarsEndedBy(treaty: TreatyId) = getWarStorage()
    .getAll()
    .filter { it.status.treaty() == treaty }