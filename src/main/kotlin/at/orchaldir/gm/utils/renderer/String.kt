package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions


fun renderWrappedString(
    renderer: LayerRenderer,
    string: String,
    center: Point2d,
    width: Distance,
    options: RenderStringOptions,
) {
    val split = string.split(' ')
    val lines = mutableListOf<String>()
    val maxWidth = width.toMeters()
    var line = ""

    for (word in split) {
        val newLine = if (line.isEmpty()) {
            word
        } else {
            "$line $word"
        }

        val length = calculateLength(newLine, options)

        if (length > maxWidth) {
            if (line.isEmpty()) {
                lines.add(word)
            } else {
                lines.add(line)
                line = word
            }
        } else {
            line = newLine
        }
    }

    if (line.isNotEmpty()) {
        lines.add(line)
    }

    val yOffset = (lines.size - 1) / 2.0f
    val step = Point2d(0.0f, options.size)
    var currentCenter = center - step * yOffset

    for (line in lines) {
        renderer.renderString(line, currentCenter, zero(), options)

        currentCenter += step
    }
}

fun calculateLength(text: String, options: RenderStringOptions): Float {
    return options.size * calculatePicaSize(text) / 1000.0f
}

fun calculatePicaSize(text: String): Int {
    val lookup = " .:,;'^`!|jl/\\i-()JfIt[]?{}sr*a\"ce_gFzLxkP+0123456789<=>~qvy\$SbduEphonTBCXY#VRKZN%GUAHD@OQ&wmMW"
    var result = 0
    for (c in text) {
        val index = lookup.indexOf(c)
        result += (if (index < 0) 60 else index) * 7 + 200
    }
    return result
}