package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseLong
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.formatWeight
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldWeight(name: String, weight: Weight) {
    field(name, weight.toString())
}

// edit

fun HtmlBlockTag.selectWeight(
    label: String,
    param: String,
    current: Weight,
    minValue: Int,
    maxValue: Int,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    field(label) {
        selectWeight(param, current, minValue, maxValue, prefix, update)
    }
}

fun HtmlBlockTag.selectWeight(
    param: String,
    current: Weight,
    minValue: Int,
    maxValue: Int,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    val text = formatWeight(current.value())
    val currentValue = current.convertTo(prefix).toInt()
    selectInt(currentValue, minValue, maxValue, 1, param, update)
    +" ($text)"
}

// parse

fun parseWeight(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Int = 0,
) = Weight.from(prefix, parseInt(parameters, param, default))
