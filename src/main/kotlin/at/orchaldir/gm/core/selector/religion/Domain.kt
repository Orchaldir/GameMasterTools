package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.God

fun State.canDeleteDomain(domain: DomainId) = DeleteResult(domain)
    .addElements(getGodsWith(domain))

fun State.countDomains(job: JobId) = getDomainStorage()
    .getAll()
    .count { domain -> domain.jobs.contains(job) }

fun State.countDomains(spell: SpellId) = getDomainStorage()
    .getAll()
    .count { domain -> domain.spells.contains(spell) }

fun countEachDomain(gods: Collection<God>) = gods
    .flatMap { it.domains }
    .groupingBy { it }
    .eachCount()

fun State.getDomainsAssociatedWith(id: JobId) = getDomainStorage()
    .getAll()
    .filter { it.jobs.contains(id) }

fun State.getDomainsAssociatedWith(id: SpellId) = getDomainStorage()
    .getAll()
    .filter { it.spells.contains(id) }

