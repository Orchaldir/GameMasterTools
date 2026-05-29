package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickPattern
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor

fun visualizeSingleBrickGrammar(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = when (grammar.pattern) {
    SingleBrickPattern.BasketWeaveSingle -> doNothing()
    SingleBrickPattern.BasketWeaveDouble -> doNothing()
    SingleBrickPattern.Grid -> visualizeGrid(state, grammar, aabb, layer)
    SingleBrickPattern.Herringbone -> doNothing()
    SingleBrickPattern.Running -> visualizeBrickRows(
        state,
        grammar,
        aabb,
        layer,
    ) { x, y ->
        if (x == 0 && y % 2 == 0) {
            1
        } else {
            2
        }
    }
    SingleBrickPattern.Stack -> visualizeBrickRows(
        state,
        grammar,
        aabb,
        layer,
        { _,_ -> 2 },
    )
}

private fun visualizeGrid(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = grammar.size.process(aabb) { start, gridSize, brickSize ->
    var startOfRow = start

    repeat(gridSize.height) {
        var currentBrick = startOfRow

        repeat(gridSize.width) {
            visualizeGrammar(
                state,
                grammar.brick,
                AABB(currentBrick, brickSize),
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
        }

        startOfRow = startOfRow.addHeight(brickSize.height)
    }
}

private fun visualizeBrickRows(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
    calculateLength: (Int, Int) -> Int,
) = grammar.size.process(aabb) { start, gridSize, blockSize ->
    var startOfRow = start

    repeat(gridSize.height) { y ->
        var currentBrick = startOfRow
        var x = 0

        while(x < gridSize.width) {
            val length = calculateLength(x, y).coerceAtMost(gridSize.width - x)
            val brickSize = blockSize.replaceWidth(Factor.fromNumber(length))

            visualizeGrammar(
                state,
                grammar.brick,
                AABB(currentBrick, brickSize),
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
            x += length
        }

        startOfRow = startOfRow.addHeight(blockSize.height)
    }
}