package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.canDeleteWithPositions
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.utils.math.unit.Area
import at.orchaldir.gm.utils.math.unit.CalculatedArea
import at.orchaldir.gm.utils.math.unit.UserDefinedArea

fun State.canDeleteDistrict(district: DistrictId) = DeleteResult(district)
    .addElements(getDistricts(district))
    .apply { canDeleteWithPositions(district, it) }

fun State.getDistricts(district: DistrictId) = getDistrictStorage()
    .getAll()
    .filter { it.position.isIn(district) }

fun State.getDistricts(town: TownId) = getDistrictStorage()
    .getAll()
    .filter { it.position.isIn(town) }

fun State.getExistingDistricts(date: Date?) = getExistingElements(getDistrictStorage().getAll(), date)

fun State.calculateArea(district: District): Area = when (district.area) {
    CalculatedArea -> getDistrictStorage()
        .getAll()
        .map { other ->
            calculateArea(other)
        }
        .reduce { sum, area -> sum + area }
    is UserDefinedArea -> district.area.area
}
