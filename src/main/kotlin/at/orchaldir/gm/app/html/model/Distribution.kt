package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CENTER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.unit.*
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Weight.Companion.fromGrams
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

fun FORM.selectDistribution(
    label: String,
    param: String,
    distribution: Distribution<Distance>,
    min: Distance,
    max: Distance,
    maxOffset: Distance,
    step: Distance = fromMillimeters(1),
    update: Boolean = false,
) {
    field(label) {
        selectDistance(combine(param, CENTER), distribution.center, min, max, step, update)
        +" +- "
        selectDistance(combine(param, OFFSET), distribution.offset, ZERO, maxOffset, step, update)
    }
}

fun FORM.selectDistribution(
    label: String,
    param: String,
    distribution: Distribution<Weight>,
    min: Int,
    max: Int,
    maxOffset: Int,
    prefix: SiPrefix = SiPrefix.Base,
    update: Boolean = false,
) {
    field(label) {
        selectWeight(combine(param, CENTER), distribution.center, min, max, prefix, update)
        +" +- "
        selectWeight(combine(param, OFFSET), distribution.offset, 0, maxOffset, prefix, update)
    }
}

// parse

fun <T : SiUnit<T>> parseDistribution(
    parameters: Parameters,
    param: String,
    parseUnit: (Parameters, String) -> T,
) = Distribution(
    parseUnit(parameters, combine(param, CENTER)),
    parseUnit(parameters, combine(param, OFFSET)),
)

