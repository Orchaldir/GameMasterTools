package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.TextOptions
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.TEXT_LAYER
import at.orchaldir.gm.visualization.character.calculateSize
import at.orchaldir.gm.visualization.character.visualizeAppearance
import java.io.File

fun renderTable(
    filename: String,
    config: RenderConfig,
    appearances: List<List<Appearance>>,
) {
    val size = calculateSize(config, appearances[0][0])
    val maxColumns = appearances.maxOf { it.size }
    val totalSize = Size2d(size.width * maxColumns, size.height * appearances.size)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    appearances.forEach { row ->
        var start = startOfRow.copy()

        row.forEach { appearance ->
            val aabb = AABB(start, size)
            val state = RenderState(aabb, config, builder, true, emptyList())

            visualizeAppearance(state, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File(filename).writeText(builder.finish().export())
}

fun <C, R> renderTable(
    filename: String,
    config: RenderConfig,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    backToo: Boolean = false,
    create: (Distance, C, R) -> Pair<Appearance, List<Equipment>>,
) {
    val height = Distance(200)
    val size = config.calculateSize(height)
    val rowSize = if (backToo) {
        2
    } else {
        1
    }
    val totalSize = Size2d(size.width * columns.size, size.height * rows.size * rowSize)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()
    val textSize = size.width / 10.0f
    val textOptions = TextOptions(Color.Black.toRender(), textSize)
    val columnTextOffset = Point2d(size.width / 2.0f, textSize)
    val columnOrientation = Orientation.zero()
    val rowOrientation = Orientation.fromDegree(270.0f)
    val layer = builder.getLayer(TEXT_LAYER)

    rows.forEach { (rowName, row) ->
        var start = startOfRow.copy()

        columns.forEach { (columnName, column) ->
            val aabb = AABB(start, size)
            val (appearance, equipment) = create(height, column, row)
            val state = RenderState(aabb, config, builder, true, equipment)

            visualizeAppearance(state, appearance)

            if (backToo) {
                val startBack = start + rowStep
                val aabbBack = AABB(startBack, size)
                val stateBack = RenderState(aabbBack, config, builder, false, equipment)

                visualizeAppearance(stateBack, appearance)
            }

            val textCenter = start + columnTextOffset
            layer.renderText(columnName, textCenter, columnOrientation, textOptions)

            start += columnStep
        }

        val textCenter = Point2d(textSize, start.y + size.height / 2.0f)
        layer.renderText(rowName, textCenter, rowOrientation, textOptions)

        if (backToo) {
            layer.renderText("Back", textCenter + rowStep, rowOrientation, textOptions)
        }

        startOfRow += rowStep * rowSize
    }

    File(filename).writeText(builder.finish().export())
}

fun addNamesToBeardStyle(values: List<BeardStyle>) = values.map {
    Pair(
        when (it) {
            is Goatee -> it.goateeStyle.name
            is GoateeAndMoustache -> "${it.goateeStyle.name} + ${it.moustacheStyle.name}"
            is Moustache -> "${it.moustacheStyle.name} Moustache"
            ShavedBeard -> "Shaved"
        }, it
    )
}

fun addNamesToEyes(values: List<Eyes>) = values.map {
    Pair(
        when (it) {
            NoEyes -> "No Eyes"
            is OneEye -> "${it.size} Eye"
            is TwoEyes -> "Two Eyes"
        }, it
    )
}

fun <T> addNames(values: Collection<T>) = values.map {
    Pair(it.toString(), it)
}