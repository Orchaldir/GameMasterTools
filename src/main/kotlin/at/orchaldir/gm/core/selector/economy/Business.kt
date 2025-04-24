package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.world.getBuilding

fun State.canDelete(id: BusinessId) = getBuilding(id) == null
        && getEmployees(id).isEmpty()
        && !isCreator(id)

fun State.getBusinessesWithBuilding() = getBuildingStorage().getAll()
    .flatMap { it.purpose.getBusinesses() }
    .toSet()

fun State.getBusinessesWithoutBuilding() = getBusinessStorage().getIds() - getBusinessesWithBuilding()

fun State.getBusinesses(job: JobId) = getCharacterStorage().getAll()
    .mapNotNull {
        val employmentStatus = it.employmentStatus.current

        if (employmentStatus is Employed && employmentStatus.job == job) {
            employmentStatus.business
        } else {
            null
        }
    }
    .toSet()
    .map { getBusinessStorage().getOrThrow(it) }

fun State.getOpenBusinesses(date: Date?) = getExistingElements(getBusinessStorage().getAll(), date)
