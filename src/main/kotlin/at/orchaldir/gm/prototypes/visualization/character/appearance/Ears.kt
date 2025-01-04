package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.calculateSizeFromHeight
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance
import java.io.File

fun main() {
    val config = CHARACTER_CONFIG
    val columns: List<Size> = Size.entries
    val rows: List<EarShape> = EarShape.entries
    val height = Distance(200)
    val size = calculateSizeFromHeight(config, height)
    val totalSize = Size2d(size.width * columns.size, size.height * rows.size)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    rows.forEach { row ->
        var start = startOfRow.copy()

        columns.forEach { column ->
            val aabb = AABB(start, size)
            val state = CharacterRenderState(aabb, config, builder, true, emptyList())
            val appearance = createAppearance(height, row, column)

            visualizeAppearance(state, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File("ears.svg").writeText(builder.finish().export())
}

private fun createAppearance(height: Distance, earShape: EarShape, size: Size) =
    HeadOnly(Head(ears = NormalEars(earShape, size), eyes = TwoEyes(), skin = ExoticSkin()), height)
