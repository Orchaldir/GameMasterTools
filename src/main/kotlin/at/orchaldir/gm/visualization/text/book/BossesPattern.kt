package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.BossesPattern
import at.orchaldir.gm.core.model.item.text.book.BossesShape
import at.orchaldir.gm.core.model.item.text.book.NoBosses
import at.orchaldir.gm.core.model.item.text.book.SimpleBossesPattern
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeBossesPattern(
    state: TextRenderState,
    pattern: BossesPattern,
) {
    when (pattern) {
        is SimpleBossesPattern -> visualizeSimpleBossesPattern(state, pattern)
        is NoBosses -> doNothing()
    }
}

private fun visualizeSimpleBossesPattern(
    state: TextRenderState,
    simple: SimpleBossesPattern,
) {
    val color = simple.boss.getColor(state.state)
    val options = state.config.getLineOptions(color)
    val parts = simple.pattern.size
    val segmentHeight = fromNumber(1.0f / parts.toFloat())
    val radius = state.aabb.convertHeight(state.config.bossesRadius.convert(simple.size))
    val size = Size2d.square(radius * 2)
    var y = segmentHeight / 2.0f
    val renderer = state.renderer.getLayer()

    simple.pattern.forEach { count ->
        val segmentWidth = fromNumber(1.0f / count.toFloat())
        var x = segmentWidth / 2.0f

        repeat(count) {
            val center = state.aabb.getPoint(x, y)

            when (simple.shape) {
                BossesShape.Circle -> renderer.renderCircle(center, radius, options)
                BossesShape.Diamond -> {
                    val polygon = Polygon2d(
                        listOf(
                            center.minusHeight(radius),
                            center.minusWidth(radius),
                            center.addHeight(radius),
                            center.addWidth(radius),
                        )
                    )

                    renderer.renderPolygon(polygon, options)
                }

                BossesShape.Square -> {
                    val square = AABB.fromCenter(center, size)

                    renderer.renderRectangle(square, options)
                }
            }

            x += segmentWidth
        }

        y += segmentHeight
    }
}
