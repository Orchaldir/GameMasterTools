package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.util.math.displayAreaLookup
import at.orchaldir.gm.app.html.util.math.fieldDensity
import at.orchaldir.gm.app.html.util.math.showAreaLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.selector.util.calculateArea
import at.orchaldir.gm.core.selector.util.calculatePopulationDensity
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.AreaUnit
import at.orchaldir.gm.utils.math.unit.HasArea
import kotlinx.html.HtmlBlockTag

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.displayAreaLookup(
    state: State,
    element: ELEMENT,
    unit: AreaUnit = state.data.largeAreaUnit,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea = displayAreaLookup(element.area(), unit) {
    state.calculateArea(element)
}

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showAreaLookupDetails(
    state: State,
    element: ELEMENT,
    unit: AreaUnit = state.data.largeAreaUnit,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea = showAreaLookupDetails(element.area(), unit) {
    state.calculateArea(element)
}

fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.fieldPopulationDensity(
    state: State,
    element: ELEMENT,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea,
        ELEMENT : HasPopulation = fieldDensity(
    "Population Density",
    state.calculatePopulationDensity(element, state.data.largeAreaUnit),
    "people",
    state.data.largeAreaUnit,
)