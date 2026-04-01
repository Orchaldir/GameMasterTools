package at.orchaldir.gm.core.selector.character.name

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.getLastJob
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.name.Name

fun State.getDefaultOccupationalName(
    given: Name,
    employmentStatus: History<EmploymentStatus>,
) = getDefaultOccupationalName(given, employmentStatus.getLastJob())

fun State.getDefaultOccupationalName(
    given: Name,
    jobId: JobId?,
): String {
    val job = getJobStorage().getOptional(jobId) ?: return given.text

    return "${given.text} The ${job.name.text}"
}
