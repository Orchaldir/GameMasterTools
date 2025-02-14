package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.religion.DomainId

fun State.canDeleteDomain(domain: DomainId) = getGodsWith(domain).isEmpty()

fun State.getDomainsAssociatedWith(id: JobId) = getDomainStorage()
    .getAll()
    .filter { it.jobs.contains(id) }

