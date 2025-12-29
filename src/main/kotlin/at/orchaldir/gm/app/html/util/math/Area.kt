package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseFloat
import at.orchaldir.gm.app.html.selectFloat
import at.orchaldir.gm.utils.math.unit.Area
import at.orchaldir.gm.utils.math.unit.AreaUnit
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldArea(
    name: String,
    area: Area?,
    unit: AreaUnit,
) {
    if (area != null && area.isGreaterZero()) {
        field(name, area.toString(unit))
    }
}

// edit

fun HtmlBlockTag.selectArea(
    label: String,
    param: String,
    area: Area,
    unit: AreaUnit,
    step: Float = 0.1f,
) {
    field(label) {
        selectArea(param, area, unit, step)
    }
}

fun HtmlBlockTag.selectArea(
    param: String,
    area: Area,
    unit: AreaUnit,
    step: Float = 0.1f,
) {
    val value = area.convertTo(unit)
    selectFloat(value, 0.0f, 10000.0f, step, param)
    +" ${unit.resolveUnit()}"
}

// parse

fun parseArea(
    parameters: Parameters,
    param: String,
    unit: AreaUnit,
    default: Float = 0.0f,
) = Area.convertFrom(parseFloat(parameters, param, default), unit)
