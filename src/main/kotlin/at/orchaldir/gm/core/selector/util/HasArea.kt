package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Area
import at.orchaldir.gm.utils.math.unit.AreaUnit
import at.orchaldir.gm.utils.math.unit.CalculatedArea
import at.orchaldir.gm.utils.math.unit.HasArea
import at.orchaldir.gm.utils.math.unit.UserDefinedArea
import at.orchaldir.gm.utils.math.unit.ZERO_AREA

fun <ID : Id<ID>, ELEMENT> State.calculateArea(element: ELEMENT): Area where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea = when (val area = element.area()) {
    CalculatedArea -> calculateArea(getDistrictStorage(), element.id())
    is UserDefinedArea -> area.area
}

fun <ID : Id<ID>, ID1 : Id<ID1>, ELEMENT> State.calculateArea(
    storage: Storage<ID, ELEMENT>,
    id: ID1,
): Area where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea,
        ELEMENT : HasPosition = storage
    .getAll()
    .filter { it.position().isIn(id) }
    .map { other ->
        calculateArea(other)
    }
    .reduceOrNull { sum, area -> sum + area } ?: ZERO_AREA

fun <ID : Id<ID>, ELEMENT> State.calculateDensity(element: ELEMENT, unit: AreaUnit): Float where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea,
        ELEMENT : HasPopulation {
    val population = element.population().getTotalPopulation() ?: return 0.0f
    val area = calculateArea(element)

    return if (area.isGreaterZero()) {
        population / area.convertTo(unit)
    } else {
        0.0f
    }
}