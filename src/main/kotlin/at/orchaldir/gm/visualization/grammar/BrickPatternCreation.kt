package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import kotlin.math.ceil
import kotlin.math.floor

data class Brick(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
) {
    override fun toString() = size.format()
}

fun createBrickPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> = when (grammar.pattern) {
    BrickPattern.BasketWeave -> createBasketWeavePattern(grammar, borders)
    BrickPattern.Grid -> createGridPattern(grammar)
    BrickPattern.Running -> createRunningPattern(grammar, borders)
    BrickPattern.Stack -> createStackPattern(grammar, borders)
    else -> error("Not supported!")
}

private fun createBasketWeavePattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> {
    var index = 0
    val n = grammar.length
    val evenOffsetX = borders.calculateEvenOffsetX(grammar.size, n)
    val evenOffsetY = borders.calculateEvenOffsetY(grammar.size, n)

    return createSubSections(
        grammar.size,
        MapSize2d.square(n),
        borders,
    ) { tiles, subSectionX, subSectionY, gridSize, subBorders ->
        val x = subSectionX * n
        val y = subSectionY * n

        val isEven = (subSectionX + evenOffsetX + subSectionY + evenOffsetY) % 2

        logger.info { "subSectionX=$subSectionX subSectionY=$subSectionY x=$x y=$y isEven=$isEven" }
        logger.info { "subBorders=$subBorders" }

        if (isEven == 0) {
            addHorizontalBasketWeaveN(
                tiles,
                grammar.brick,
                x,
                y,
                gridSize,
                subBorders.apply(gridSize),
                subBorders,
                n,
            )
        } else {
            addVerticalBasketWeaveN(
                tiles,
                grammar.brick,
                x,
                y,
                gridSize,
                subBorders.apply(gridSize),
                subBorders,
                n,
            )
        }

        index += n
    }
}

private fun createGridPattern(grammar: BrickPatternGrammar): TileMap2d<Brick?> {
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

private fun createRunningPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> {
    val offset = borders.calculateTileOffsetX2(grammar.size, grammar.length)
    val halfBrick = floor(grammar.length / 2.0).toInt()

    return createRowPattern(
        grammar,
        borders,
        offset,
    ) { y ->
        if (y % 2 == 0) {
            0
        } else {
            -halfBrick
        }
    }
}

private fun createStackPattern(
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
            val (length, brickX) = createRowBrick(grammar, gridSize, borders, x)
            val tileIndex = gridSize.toIndexRisky(brickX, y)

            tiles[tileIndex] = Brick(grammar.brick, MapSize2d(length, 1))

            x = brickX + length
        }
    }

    return TileMap2d(gridSize, tiles)
}

private fun createRowBrick(
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

private fun createSubSections(
    grammarSize: GridSize,
    subSectionSize: MapSize2d,
    borders: Borders,
    addSubSection: (MutableList<Brick?>, Int, Int, MapSize2d, Borders) -> Unit,
): TileMap2d<Brick?> {
    val gridSize = grammarSize.size()
    val tiles = MutableList<Brick?>(gridSize.tiles()) { null }
    val offset = borders.calculateTileOffset(gridSize, subSectionSize)
    val gridSizeWithOffset = gridSize - offset
    val subSections = MapSize2d(
        ceil((gridSize.width - offset.width) / subSectionSize.width.toDouble()).toInt(),
        ceil((gridSize.height - offset.height) / subSectionSize.height.toDouble()).toInt(),
    )

    repeat(subSections.height) { y ->
        repeat(subSections.width) { x ->
            val subBorders = Borders(
                if (y < subSections.height - 1) {
                    false
                } else {
                    borders.bottom
                },
                if (x == 0) {
                    borders.left
                } else {
                    false
                },
                if (x < subSections.width - 1) {
                    false
                } else {
                    borders.right
                },
                if (y == 0) {
                    borders.top
                } else {
                    false
                },
                x,
                y,
            )

            addSubSection(
                tiles,
                x,
                y,
                gridSizeWithOffset,
                subBorders,
            )
        }
    }

    return TileMap2d(gridSize, tiles)
}

private fun addHorizontalBasketWeaveN(
    tiles: MutableList<Brick?>,
    brick: ShapeGrammar,
    x: Int,
    y: Int,
    gridSize: MapSize2d,
    limits: MapSize2d,
    borders: Borders,
    n: Int,
) {
    val maxLength = limits.width - x
    val blocks = MapSize2d(n.coerceAtMost(maxLength), 1)

    repeat(n) { offset ->
        val currentY = y + offset

        if (!borders.bottom || currentY < limits.height) {
            val tileIndex = gridSize.toIndexRisky(x, currentY)

            tiles[tileIndex] = Brick(brick, blocks)
        }
    }
}

private fun addVerticalBasketWeaveN(
    tiles: MutableList<Brick?>,
    brick: ShapeGrammar,
    x: Int,
    y: Int,
    gridSize: MapSize2d,
    limits: MapSize2d,
    borders: Borders,
    n: Int,
) {
    val blocks = MapSize2d(1, n)

    repeat(n) { offset ->
        val currentX = x + offset

        if (!borders.right || currentX < limits.width) {
            val tileIndex = gridSize.toIndexRisky(currentX, y)

            tiles[tileIndex] = Brick(brick, blocks)
        }
    }
}