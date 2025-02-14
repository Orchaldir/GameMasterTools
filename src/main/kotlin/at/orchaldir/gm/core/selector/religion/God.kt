package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId

fun State.canDeleteGod(god: GodId) = true

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


