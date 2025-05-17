package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteLegalCode(code: LegalCodeId) = getRealmsWithAnyLegalCode(code) == 0

fun State.getExistingLegalCodes(date: Date?) = getExistingElements(getLegalCodeStorage().getAll(), date)
