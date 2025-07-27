package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteDistrict(code: DistrictId) = false

fun State.getExistingDistricts(date: Date?) = getExistingElements(getDistrictStorage().getAll(), date)
