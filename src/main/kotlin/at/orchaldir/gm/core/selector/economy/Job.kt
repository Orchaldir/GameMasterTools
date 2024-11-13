package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees

fun State.canDelete(job: JobId) = getEmployees(job).isEmpty()
        && getPreviousEmployees(job).isEmpty()

fun countJobs(characters: Collection<Character>) = characters
    .map { it.employmentStatus.current.getJob() }
    .groupingBy { it }
    .eachCount()



