package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CENTER
import at.orchaldir.gm.app.OFFSET
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.SiUnit
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
    step: Distance = Distance(1),
    update: Boolean = false,
) {
    field(label) {
        selectDistance(combine(param, CENTER), distribution.center, min, max, step, update)
        +" +- "
        selectDistance(combine(param, OFFSET), distribution.offset, Distance(0), maxOffset, step, update)
    }
}

// parse

fun parseDistribution(parameters: Parameters, param: String) = Distribution(
    parseDistance(parameters, combine(param, CENTER)),
    parseDistance(parameters, combine(param, OFFSET)),
)

