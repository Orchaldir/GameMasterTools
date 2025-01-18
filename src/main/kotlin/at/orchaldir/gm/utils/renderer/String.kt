package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.VerticalAlignment

fun renderWrappedStrings(
    renderer: LayerRenderer,
    entries: List<Pair<String, RenderStringOptions>>,
    position: Point2d,
    maxWidth: Distance,
    alignment: VerticalAlignment,
) {
    val orderedEntries = when (alignment) {
        VerticalAlignment.Top, VerticalAlignment.Center -> entries
        VerticalAlignment.Bottom -> entries.reversed()
    }
    val direction = when (alignment) {
        VerticalAlignment.Top, VerticalAlignment.Center -> 1.0f
        VerticalAlignment.Bottom -> -1.0f
    }
    val linesEntries = orderedEntries.map { Pair(wrapString(it.first, maxWidth, it.second.size), it.second) }
    var currentPosition = when (alignment) {
        VerticalAlignment.Top, VerticalAlignment.Bottom -> position
        VerticalAlignment.Center -> {
            val totalSize = linesEntries.fold(0.0f) { value, (lines, options) ->
                value + lines.size * options.size
            }
            position - Point2d(0.0f, totalSize / 2)
        }
    }

    for ((lines, options) in linesEntries) {
        renderWrappedString(renderer, lines, currentPosition, options)

        currentPosition += Point2d(0.0f, lines.size * options.size * direction)
    }
}

fun renderWrappedString(
    renderer: LayerRenderer,
    string: String,
    position: Point2d,
    maxWidth: Distance,
    options: RenderStringOptions,
) {
    val lines = wrapString(string, maxWidth, options.size)

    renderWrappedString(renderer, lines, position, options)
}

fun renderWrappedString(
    renderer: LayerRenderer,
    lines: List<String>,
    position: Point2d,
    options: RenderStringOptions,
) {

    val step = Point2d(0.0f, options.size)
    var currentPosition = when (options.verticalAlignment) {
        VerticalAlignment.Top -> position
        VerticalAlignment.Center -> {
            val yOffset = (lines.size - 1) / 2.0f
            position - step * yOffset
        }

        VerticalAlignment.Bottom -> position - step * (lines.size - 1)
    }

    for (line in lines) {
        renderer.renderString(line, currentPosition, zero(), options)

        currentPosition += step
    }
}

fun wrapString(
    string: String,
    maxWidth: Distance,
    fontSize: Float,
): List<String> {
    val split = string.split(' ')
    val lines = mutableListOf<String>()
    val maxLength = maxWidth.toMeters()
    var line = ""

    for (word in split) {
        val newLine = if (line.isEmpty()) {
            word
        } else {
            "$line $word"
        }

        val length = calculateLength(newLine, fontSize)

        if (length > maxLength) {
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

    return lines
}

fun calculateLength(text: String, fontSize: Float): Float {
    return fontSize * calculatePicaSize(text) / 1000.0f
}

private fun calculatePicaSize(text: String): Int {
    val lookup = " .:,;'^`!|jl/\\i-()JfIt[]?{}sr*a\"ce_gFzLxkP+0123456789<=>~qvy\$SbduEphonTBCXY#VRKZN%GUAHD@OQ&wmMW"
    var result = 0
    for (c in text) {
        val index = lookup.indexOf(c)
        result += (if (index < 0) 60 else index) * 7 + 200
    }
    return result
}