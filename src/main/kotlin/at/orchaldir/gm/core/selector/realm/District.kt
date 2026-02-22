package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.canDeleteWithPositions
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteDistrict(district: DistrictId) = DeleteResult(district)
    .addElements(getDistricts(district))
    .apply { canDeleteWithPositions(district, it) }

fun State.getDistricts(district: DistrictId) = getDistrictStorage()
    .getAll()
    .filter { it.position.isIn(district) }

fun State.getDistricts(town: SettlementId) = getDistrictStorage()
    .getAll()
    .filter { it.position.isIn(town) }

fun State.getExistingDistricts(date: Date?) = getExistingElements(getDistrictStorage().getAll(), date)

