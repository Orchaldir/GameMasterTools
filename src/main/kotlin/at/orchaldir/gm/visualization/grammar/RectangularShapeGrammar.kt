package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.visualization.utils.createRectangularShapePolygon

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