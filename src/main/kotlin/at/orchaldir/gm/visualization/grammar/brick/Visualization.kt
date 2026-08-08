package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.AshlarGrammar
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.visualization.grammar.Borders
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar


fun visualizeAshlarPattern(
    state: GrammarRenderState,
    grammar: AshlarGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
) {
    val gridmap = createAshlarPattern(state.numberGenerator, grammar, borders)

    visualizeBrickTilemap(state, gridmap, aabb, layer)
}

fun visualizeBrickPattern(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
) {
    val gridmap = createBrickPattern(grammar, borders)

    visualizeBrickTilemap(state, gridmap, aabb, layer)
}

fun visualizeBrickTilemap(
    state: GrammarRenderState,
    gridmap: TileMap2d<BrickTile>,
    aabb: AABB,
    layer: Int,
) {
    val renderer = TileMap2dRenderer(aabb.size, gridmap.size)

    renderer.render(gridmap, aabb.start) { index, blockAabb, borders, brick ->
        if (brick is BrickStart) {
            val brickSize = blockAabb.size * brick.size
            val brickAabb = blockAabb.copy(size = brickSize)
            val brickState = state.addSeed(index)

            visualizeShapeGrammar(brickState, brick.grammar, brickAabb, borders, layer)
        }
    }
}
