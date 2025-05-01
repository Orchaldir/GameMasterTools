package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.countDomains

fun State.canDelete(job: JobId) = getEmployees(job).isEmpty()
        && getPreviousEmployees(job).isEmpty()
        && countDomains(job) == 0

fun countEachJob(characters: Collection<Character>) = characters
    .map { it.employmentStatus.current.getJob() }
    .groupingBy { it }
    .eachCount()

fun State.countJobs(spell: SpellId) = getJobStorage()
    .getAll()
    .count { it.spells.contains(spell) }

fun State.countJobs(standard: StandardOfLivingId) = getJobStorage()
    .getAll()
    .count { it.income.hasStandard(standard) }

fun State.getJobsContaining(spell: SpellId) = getJobStorage()
    .getAll()
    .filter { it.spells.contains(spell) }

fun State.getJobs(standard: StandardOfLivingId) = getJobStorage()
    .getAll()
    .filter { it.income.hasStandard(standard) }



