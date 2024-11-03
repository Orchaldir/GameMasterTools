package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.metersOnly
import at.orchaldir.gm.utils.math.millimetersOnly
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldDistance(name: String, distance: Distance) {
    field(name, formatDistance(distance))
}

fun formatDistance(distance: Distance) =
    formatMillimetersAsMeters(distance.millimeters)

fun formatMillimetersAsMeters(millimeters: Int) =
    String.format("%d.%d m", metersOnly(millimeters), millimetersOnly(millimeters))

fun FORM.selectDistance(
    text: String,
    param: String,
    distance: Distance,
    minValue: Int,
    maxVale: Int,
    stepValue: Int = 1,
) {
    val values = (minValue..maxVale step stepValue).toList()
    selectValue(text, param, values) { height ->
        label = formatMillimetersAsMeters(height)
        value = height.toString()
        selected = height == distance.millimeters
    }
}

