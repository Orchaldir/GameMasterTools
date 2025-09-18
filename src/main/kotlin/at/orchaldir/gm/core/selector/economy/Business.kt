package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.item.getTextsPublishedBy
import at.orchaldir.gm.core.selector.util.canDeleteCreator
import at.orchaldir.gm.core.selector.util.canDeleteOwner
import at.orchaldir.gm.core.selector.util.canDeletePosition
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteBusiness(business: BusinessId) = DeleteResult(business)
    .addElements(getEmployees(business))
    .addElements(getPreviousEmployees(business))
    .addElements(getTextsPublishedBy(business))
    .apply { canDeleteCreator(business, it) }
    .apply { canDeleteOwner(business, it) }
    .apply { canDeletePosition(business, it) }

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
