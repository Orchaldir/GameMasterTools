package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.trait.PersonalityTraitId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.canDeleteCreator
import at.orchaldir.gm.core.selector.util.canDeleteTargetOfBelief
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane

fun State.canDeleteGod(god: GodId) = DeleteResult(god)
    .addElement(getHeartPlane(god))
    .addElement(getPrisonPlane(god))
    .addElements(getHolidays(god))
    .addElements(getMasksOf(god))
    .addElements(getPantheonsContaining(god))
    .apply { canDeleteCreator(god, it) }
    .apply { canDeleteTargetOfBelief(god, it) }

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


