package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.CENTER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun <T : SiUnit<T>> HtmlBlockTag.showDistribution(
    label: String,
    distribution: Distribution<T>,
) {
    field(label, distribution.display())
}

// edit

fun FORM.selectDistanceDistribution(
    label: String,
    param: String,
    distribution: Distribution<Distance>,
    min: Distance,
    max: Distance,
    prefix: SiPrefix = SiPrefix.Base,
) = selectDistanceDistribution(
    label,
    param,
    distribution,
    min.convertToLong(prefix),
    max.convertToLong(prefix),
    prefix,
)

fun FORM.selectDistanceDistribution(
    label: String,
    param: String,
    distribution: Distribution<Distance>,
    minHeight: Long,
    maxHeight: Long,
    prefix: SiPrefix = SiPrefix.Base,
) {
    field(label) {
        selectDistance(
            combine(param, CENTER),
            distribution.center,
            minHeight,
            maxHeight,
            prefix,
        )
        +" +- "
        selectFactor(
            combine(param, OFFSET),
            distribution.offset,
            MIN_DISTRIBUTION_FACTOR,
            MAX_DISTRIBUTION_FACTOR,
        )
    }
}

// parse

fun <T : SiUnit<T>> parseDistribution(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    parseUnit: (Parameters, String, SiPrefix) -> T,
) = Distribution(
    parseUnit(parameters, combine(param, CENTER), prefix),
    parseFactor(parameters, combine(param, OFFSET), MIN_DISTRIBUTION_FACTOR),
)

