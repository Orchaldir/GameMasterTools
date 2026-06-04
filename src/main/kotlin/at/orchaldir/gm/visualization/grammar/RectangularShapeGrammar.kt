package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.halfSegment
import at.orchaldir.gm.utils.math.subdividePolygon
import at.orchaldir.gm.visualization.grammar.RectangularShape.*

fun visualizeRectangularShapeGrammar(
    state: GrammarRenderState,
    grammar: RectangularShapeGrammar,
    aabb: AABB,
    layer: Int,
) {
    val renderer = state.renderer.getLayer(layer)
    val polygon = createRectangularShapePolygon(grammar.shape, aabb)
    val options = state.getFillAndBorder(grammar.part)

    if (grammar.shape.isRounded()) {
        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderPolygon(polygon, options)
    }
}

private fun createRectangularShapePolygon(
    shape: RectangularShape,
    aabb: AABB,
) = when (shape) {
    Rectangle, Ellipse -> Polygon2d(aabb.getCorners())
    RoundedRectangle -> Polygon2d(subdividePolygon(aabb.getCorners(), 1, ::halfSegment))
}