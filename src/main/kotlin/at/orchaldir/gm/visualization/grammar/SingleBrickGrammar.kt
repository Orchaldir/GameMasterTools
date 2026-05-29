package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickPattern
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB

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
    SingleBrickPattern.Running -> doNothing()
    SingleBrickPattern.Stack -> doNothing()
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