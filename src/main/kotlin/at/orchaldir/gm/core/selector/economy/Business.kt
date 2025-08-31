package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator

fun State.canDelete(id: BusinessId) = getEmployees(id).isEmpty()
        && !isCreator(id)

fun State.getBusinesses(job: JobId) = getCharacterStorage()
    .getAll()
    .mapNotNull {
        val employmentStatus = it.employmentStatus.current

        if (employmentStatus.hasJob(job)) {
            employmentStatus.getBusiness()
        } else {
            null
        }
    }
    .toSet()

fun State.getOpenBusinesses(date: Date?) = getExistingElements(getBusinessStorage().getAll(), date)
