package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.CENTER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.utils.math.Value
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun <T : Value<T>> HtmlBlockTag.fieldVariance(
    label: String,
    variance: Variance<T>,
) {
    field(label, variance.display())
}

// edit

fun HtmlBlockTag.selectOrientationVariance(
    label: String,
    param: String,
    variance: Variance<Orientation>,
    minCenter: Orientation,
    maxCenter: Orientation,
    maxOffset: Orientation,
    step: Orientation = fromDegrees(1),
) = selectVariance(
    label,
    param,
    variance,
    minCenter,
    maxCenter,
    maxOffset,
) { param, current, minValue, maxValue ->
    selectOrientation(
        param,
        current,
        minValue,
        maxValue,
        step,
    )
}

fun <T : Value<T>> HtmlBlockTag.selectVariance(
    label: String,
    param: String,
    variance: Variance<T>,
    minCenter: T,
    maxCenter: T,
    maxOffset: T,
    selectValue: (String, T, T, T) -> Unit,
) {
    field(label) {
        selectValue(
            combine(param, CENTER),
            variance.center,
            minCenter,
            maxCenter,
        )
        +" +- "
        selectValue(
            combine(param, OFFSET),
            variance.offset,
            maxOffset.zero(),
            maxOffset,
        )
    }
}

// parse

fun parseOrientationVariance(
    parameters: Parameters,
    param: String,
    defaultCenter: Orientation,
) = parseVariance(
    parameters,
    param,
    defaultCenter,
    ZERO_ORIENTATION,
    ::parseOrientation,
)

fun <T : Value<T>> parseVariance(
    parameters: Parameters,
    param: String,
    defaultCenter: T,
    defaultOffset: T,
    parseUnit: (Parameters, String, T) -> T,
) = Variance(
    parseUnit(parameters, combine(param, CENTER), defaultCenter),
    parseUnit(parameters, combine(param, OFFSET), defaultOffset),
)

