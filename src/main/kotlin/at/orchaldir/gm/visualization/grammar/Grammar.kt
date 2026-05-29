package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.DoNothingGrammar
import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB

fun visualizeGrammar(
    state: GrammarRenderState,
    grammar: Grammar,
    aabb: AABB,
    layer: Int = 0,
): Unit = when (grammar) {
    is RectangularShapeGrammar -> visualizeRectangularShapeGrammar(state, grammar, aabb, layer)
    is SingleBrickGrammar -> visualizeSingleBrickGrammar(state, grammar, aabb, layer)
    DoNothingGrammar -> doNothing()
}