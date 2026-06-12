package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d

fun visualizeShapeGrammar(
    state: GrammarRenderState,
    grammar: ShapeGrammar,
    aabb: AABB,
    borders: Borders = Borders(),
    layer: Int = 0,
): Unit = when (grammar) {
    is BrickPatternGrammar -> visualizeBrickPatternGrammar(state, grammar, aabb, borders , layer)
    DoNothingShapeGrammar -> doNothing()
    is RectangularShapeGrammar -> visualizeRectangularShapeGrammar(state, grammar, aabb, layer)
    is ShrinkGrammar -> visualizeShapeGrammar(
        state,
        grammar.grammar,
        aabb.shrink(grammar.factor),
        borders,
        layer,
    )
}

fun visualizeShapeGrammar(
    state: GrammarRenderState,
    grammar: ShapeGrammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    blocks: MapSize2d,
    limits: MapSize2d,
    borders: Borders = Borders(),
    layer: Int = 0,
    shrinkFactor: Factor? = null,
): Unit = when (grammar) {
    is BrickPatternGrammar -> doNothing()
    DoNothingShapeGrammar -> doNothing()
    is RectangularShapeGrammar -> {
        logger.info { "x=$x y=$y limits=$limits blocks=$blocks" }
        val limitedBlocks = blocks.limit(x, y, limits)
        val aabbStart = Point2d.fromGrid(blockSize, gridStart, x, y)
        val aabbSize = blockSize * limitedBlocks
        val aabb = AABB(aabbStart, aabbSize)
        val shrunkenAabb = if (shrinkFactor != null) {
            aabb.shrinkRelativeToSmallerSide(shrinkFactor)
        } else {
            aabb
        }

        visualizeRectangularShapeGrammar(state, grammar, shrunkenAabb, layer)
    }

    is ShrinkGrammar -> visualizeShapeGrammar(
        state,
        grammar.grammar,
        gridStart,
        blockSize,
        x,
        y,
        blocks,
        limits,
        borders,
        layer,
        grammar.factor,
    )
}