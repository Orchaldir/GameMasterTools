package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.AshlarGrammar
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders

fun createAshlarPattern(
    grammar: AshlarGrammar,
    borders: Borders,
): TileMap2d<BrickTile> {
    val gridSize = grammar.size.size()
    val builder = SimpleBrickMapBuilder(gridSize, borders)

    return builder.finish()
}
