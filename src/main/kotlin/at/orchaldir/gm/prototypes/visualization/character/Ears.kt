package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.calculateSizeFromHeight
import at.orchaldir.gm.visualization.character.visualizeAppearance
import java.io.File

fun main() {
    val config = RENDER_CONFIG
    val columns: List<Size> = Size.entries
    val rows: List<EarShape> = EarShape.entries
    val height = Distance(0.2f)
    val size = calculateSizeFromHeight(config, height)
    val totalSize = Size2d(size.width * columns.size, size.height * rows.size)
    val builder = SvgBuilder.create(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    rows.forEach { row ->
        var start = startOfRow.copy()

        columns.forEach { column ->
            val aabb = AABB(start, size)
            val appearance = createAppearance(height, row, column)

            visualizeAppearance(builder, config, aabb, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File("ears.svg").writeText(builder.finish().export())
}

private fun createAppearance(height: Distance, earShape: EarShape, size: Size) =
    HeadOnly(Head(NormalEars(earShape, size), eyes = TwoEyes(), skin = ExoticSkin()), height)
