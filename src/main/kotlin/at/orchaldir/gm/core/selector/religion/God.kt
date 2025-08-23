package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.canDeleteHasBelief
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane

fun State.canDeleteGod(god: GodId) = !isCreator(god)
        && getHeartPlane(god) == null
        && getHolidays(god).isEmpty()
        && getPrisonPlane(god) == null
        && canDeleteHasBelief(god)
        && getMasksOf(god).isEmpty()
        && canDeleteHasBelief(god)

fun State.getGodsAssociatedWith(id: JobId): List<God> {
    val domains = getDomainsAssociatedWith(id).map { it.id }

    return getGodStorage()
        .getAll()
        .filter { god ->
            god.domains.any { domains.contains(it) }
        }
}

fun State.getGodsWith(id: DomainId) = getGodStorage()
    .getAll()
    .filter { it.domains.contains(id) }

fun State.getGodsWith(id: PersonalityTraitId) = getGodStorage()
    .getAll()
    .filter { it.personality.contains(id) }

fun State.getMasksOf(god: GodId) = getGodStorage()
    .getAll()
    .filter { it.authenticity.isMaskOf(god) }


