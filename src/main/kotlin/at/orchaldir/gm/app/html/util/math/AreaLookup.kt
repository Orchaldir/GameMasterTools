package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.AREA
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayAreaLookup(
    lookup: AreaLookup,
    unit: AreaUnit,
    calculate: () -> Area,
) {
    val area = when (lookup) {
        CalculatedArea -> calculate()
        is UserDefinedArea -> lookup.area
    }

    +area.toString(unit)
}

fun HtmlBlockTag.showAreaLookupDetails(
    lookup: AreaLookup,
    unit: AreaUnit,
    calculate: () -> Area,
) {
    showDetails("Area", true) {
        field("Type", lookup.getType())

        when (lookup) {
            CalculatedArea -> fieldArea("Calculated Area", calculate(), unit)
            is UserDefinedArea -> fieldArea("User Defined Area", lookup.area, unit)
        }
    }
}

// edit

fun HtmlBlockTag.selectAreaLookup(
    lookup: AreaLookup,
    unit: AreaUnit,
    param: String = AREA,
) {
    showDetails("Area", true) {
        selectValue("Type", combine(param, TYPE), AreaLookupType.entries, lookup.getType())

        when (lookup) {
            CalculatedArea -> doNothing()
            is UserDefinedArea -> selectArea(
                "User Defined Area",
                param,
                lookup.area,
                unit,
            )
        }
    }
}

// parse

fun parseAreaLookup(
    parameters: Parameters,
    unit: AreaUnit,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), AreaLookupType.Calculated)) {
    AreaLookupType.Calculated -> CalculatedArea
    AreaLookupType.UserDefined -> UserDefinedArea(
        parseArea(parameters, param, unit),
    )
}
