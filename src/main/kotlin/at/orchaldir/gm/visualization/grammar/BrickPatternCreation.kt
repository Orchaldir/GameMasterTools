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
    val offset = borders.calculateTileOffsetX2(grammar.size, grammar.length)

    return createRowPattern(
        grammar,
        borders,
        offset,
        { _ -> 0 },
    )
}

private fun createRowPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
    offset: Int,
    calculateStartOfRow: (Int) -> Int,
): TileMap2d<Brick?> {
    val gridSize = grammar.size.size()
    val tiles = MutableList<Brick?>(gridSize.tiles()) { null }

    repeat(gridSize.height) { y ->
        var x = calculateStartOfRow(y) + offset

        while (x < gridSize.width) {
            val (length, brickX) = calculateRowBrick(grammar, gridSize, borders, x)
            val tileIndex = gridSize.toIndexRisky(brickX, y)

            tiles[tileIndex] = Brick(grammar.brick, MapSize2d(length, 1))

            x = brickX + length
        }
    }

    return TileMap2d(gridSize, tiles)
}

private fun calculateRowBrick(
    grammar: BrickPatternGrammar,
    gridSize: MapSize2d,
    borders: Borders,
    x: Int,
): Pair<Int, Int> {
    var outputX = x
    val length = if (x < 0) {
        val remainingLength = grammar.length + x

        if (borders.left) {
            outputX = 0

            remainingLength
        } else {
            outputX += grammar.length

            grammar.length
        }
    } else if (borders.right && x + grammar.length > gridSize.width) {
        val maxLength = gridSize.width - x

        grammar.length.coerceAtMost(maxLength)
    } else {
        grammar.length
    }

    return Pair(length, outputX)
}