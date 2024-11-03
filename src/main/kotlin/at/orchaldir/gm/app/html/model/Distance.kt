package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.metersOnly
import at.orchaldir.gm.utils.math.millimetersOnly
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldDistance(name: String, distance: Distance) {
    field(name, formatDistance(distance))
}

fun formatDistance(distance: Distance) =
    formatMillimetersAsMeters(distance.millimeters)

fun formatMillimetersAsMeters(millimeters: Int) =
    String.format("%d.%03d m", metersOnly(millimeters), millimetersOnly(millimeters))

fun HtmlBlockTag.selectDistance(
    label: String,
    param: String,
    distance: Distance,
    minValue: Distance,
    maxVale: Distance,
    stepValue: Distance = Distance(1),
    update: Boolean = false,
) {
    field(label) {
        selectDistance(param, distance, minValue, maxVale, stepValue, update)
    }
}

fun HtmlBlockTag.selectDistance(
    param: String,
    distance: Distance,
    minValue: Distance,
    maxVale: Distance,
    stepValue: Distance = Distance(1),
    update: Boolean = false,
) {
    val values = (minValue.millimeters..maxVale.millimeters step stepValue.millimeters).toList()
    selectValue(param, values, update) { height ->
        label = formatMillimetersAsMeters(height)
        value = height.toString()
        selected = height == distance.millimeters
    }
}


