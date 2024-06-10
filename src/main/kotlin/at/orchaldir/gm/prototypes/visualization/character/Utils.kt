package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig
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

            visualizeAppearance(builder, config, aabb, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File(filename).writeText(builder.finish().export())
}

fun <C, R> renderTable(
    filename: String,
    config: RenderConfig,
    rows: List<R>,
    columns: List<C>,
    create: (Distance, C, R) -> Appearance,
) {
    val height = Distance(0.2f)
    val size = config.calculateSize(height)
    val totalSize = Size2d(size.width * columns.size, size.height * rows.size)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    rows.forEach { row ->
        var start = startOfRow.copy()

        columns.forEach { column ->
            val aabb = AABB(start, size)
            val appearance = create(height, column, row)

            visualizeAppearance(builder, config, aabb, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File(filename).writeText(builder.finish().export())
}