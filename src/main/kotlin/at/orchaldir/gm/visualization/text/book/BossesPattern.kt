package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
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
    val options = FillAndBorder(simple.color.toRender(), state.config.line)
    val parts = simple.pattern.size
    val segmentHeight = Factor(1.0f / parts.toFloat())
    val radius = state.aabb.convertHeight(state.config.bossesRadius.convert(simple.size))
    var y = segmentHeight / 2.0f
    val renderer = state.renderer.getLayer()

    simple.pattern.forEach { count ->
        val segmentWidth = Factor(1.0f / count.toFloat())
        var x = segmentWidth / 2.0f

        repeat(count) {
            val center = state.aabb.getPoint(x, y)

            renderer.renderCircle(center, radius, options)

            x += segmentWidth
        }

        y += segmentHeight
    }
}
