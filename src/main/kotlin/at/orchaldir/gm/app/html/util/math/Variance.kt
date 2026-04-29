package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.CENTER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.utils.math.Value
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.*
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
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
    minOffset: Orientation,
    maxOffset: Orientation,
    step: Orientation = fromDegrees(1),
) = selectVariance(
    label,
    param,
    variance,
    minCenter,
    maxCenter,
    minOffset,
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
    minOffset: T,
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
            minOffset,
            maxOffset,
        )
    }
}

// parse

fun parseOrientationVariance(
    parameters: Parameters,
    param: String,
) = parseVariance(
    parameters,
    param,
    ::parseOrientation,
)

fun <T : Value<T>> parseVariance(
    parameters: Parameters,
    param: String,
    parseUnit: (Parameters, String) -> T,
) = Variance(
    parseUnit(parameters, combine(param, CENTER)),
    parseUnit(parameters, combine(param, OFFSET)),
)

