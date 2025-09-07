package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteLegalCode(code: LegalCodeId) = DeleteResult(code)
    .addElements(getRealmsWithLegalCode(code))

fun State.getExistingLegalCodes(date: Date?) = getExistingElements(getLegalCodeStorage().getAll(), date)
