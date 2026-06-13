package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d

data class Brick(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
)

fun createGridPattern(grammar: BrickPatternGrammar): TileMap2d<Brick?> {
    val tilemapSize = grammar.size.size()
    val brickSize = MapSize2d.square(1)
    val tiles = mutableListOf<Brick?>()

    repeat(tilemapSize.height) { y ->
        repeat(tilemapSize.width) {
            tiles.add(Brick(grammar.brick, brickSize))
        }
    }

    return TileMap2d(tilemapSize, tiles)
}

fun createStackPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> {
    val offset = borders.calculateTileOffsetX(grammar.size, grammar.length)

    return createRows(
        grammar,
        borders,
        offset,
        { _ -> 0 },
    )
}

private fun createRows(
    grammar: BrickPatternGrammar,
    borders: Borders,
    offset: Int,
    calculateStartX: (Int) -> Int,
): TileMap2d<Brick?> {
    val gridSize = grammar.size.size()
    val tiles = MutableList<Brick?>(gridSize.tiles()) { null }

    repeat(gridSize.height) { y ->
        var x = calculateStartX(y)

        while (x + offset < gridSize.width) {
            val length = if (x + offset < 0) {
                val remainingLength = grammar.length + x

                if (borders.left) {
                    x = 0

                    remainingLength
                } else {
                    x += grammar.length

                    grammar.length
                }
            } else {
                grammar.length
            }

            val tileIndex = gridSize.toIndexRisky(x, y)

            tiles[tileIndex] = Brick(grammar.brick, MapSize2d(length, 1))

            x += length
        }
    }

    return TileMap2d(gridSize, tiles)
}