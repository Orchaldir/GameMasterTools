package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.canDeletePosition
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteDistrict(district: DistrictId) = DeleteResult(district)
    .apply { canDeletePosition(district, it) }

fun State.getDistricts(town: TownId) = getDistrictStorage()
    .getAll()
    .filter { it.town == town }

fun State.getExistingDistricts(date: Date?) = getExistingElements(getDistrictStorage().getAll(), date)
