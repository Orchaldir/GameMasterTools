package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.DoNothingGrammar
import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d

fun visualizeGrammar(
    state: GrammarRenderState,
    grammar: Grammar,
    aabb: AABB,
    layer: Int = 0,
): Unit = when (grammar) {
    is RectangularShapeGrammar -> visualizeRectangularShapeGrammar(state, grammar, aabb, layer)
    is BrickPatternGrammar -> visualizeSingleBrickGrammar(state, grammar, aabb, layer)
    DoNothingGrammar -> doNothing()
}

fun visualizeGrammar(
    state: GrammarRenderState,
    grammar: Grammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    blocks: MapSize2d,
    limits: MapSize2d,
    layer: Int = 0,
): Unit = when (grammar) {
    is RectangularShapeGrammar -> {
        val limitedBlocks = blocks.limit(x, y, limits)
        val aabbStart = Point2d.fromGrid(blockSize, gridStart, x, y)
        val aabbSize = blockSize * limitedBlocks

        visualizeRectangularShapeGrammar(state, grammar, AABB(aabbStart, aabbSize), layer)
    }
    is BrickPatternGrammar -> doNothing()
    DoNothingGrammar -> doNothing()
}