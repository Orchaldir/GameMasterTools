package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.visualization.grammar.Borders
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar

fun visualizeBrickPatternGrammar(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
) {
    val gridmap = createBrickPattern(grammar, borders)
    val renderer = TileMap2dRenderer(aabb.size, grammar.size.size())

    renderer.render(gridmap, aabb.start) { _, blockAabb, borders, brick ->
        if (brick != null) {
            val brickSize = blockAabb.size * brick.size
            val brickAabb = blockAabb.copy(size = brickSize)

            visualizeShapeGrammar(state, brick.grammar, brickAabb, borders, layer)
        }
    }
}
