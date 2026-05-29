package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.utils.math.AABB

fun visualizeSingleBrickGrammar(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) {
    val renderer = state.renderer.getLayer(layer)

}