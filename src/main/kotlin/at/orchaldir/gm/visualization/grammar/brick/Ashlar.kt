package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.AshlarGrammar
import at.orchaldir.gm.utils.RepeatableNumberGenerator
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders

fun createAshlarPattern(
    numberGenerator: RepeatableNumberGenerator,
    grammar: AshlarGrammar,
    borders: Borders,
): TileMap2d<BrickTile> {
    val gridSize = grammar.size.size()
    val builder = SimpleBrickMapBuilder(gridSize, borders)
    var index = 0

    repeat(gridSize.height) { y ->
        repeat(gridSize.width) { x ->
            if(builder.map()[index].isFree()) {
                addAshlar(numberGenerator, grammar, gridSize, builder, x, y, index)
            }

            index++
        }
    }

    return builder.finish()
}

private fun addAshlar(
    numberGenerator: RepeatableNumberGenerator,
    grammar: AshlarGrammar,
    gridSize: MapSize2d,
    builder: SimpleBrickMapBuilder,
    x: Int,
    y: Int,
    index: Int,
) {
    var maxWidth = grammar.brickWidth.coerceAtMost(gridSize.width - x)
    val maxHeight = grammar.brickHeight.coerceAtMost(gridSize.height - y)
    val maxWidthPerRow = mutableListOf<Int>()

    for (offsetY in 0..<maxHeight) {
        val newMaxWidth = checkRow(maxWidth, builder, x, y, offsetY)

        maxWidthPerRow.add(newMaxWidth)
        maxWidth = maxWidth.coerceAtMost(newMaxWidth)

        if (maxWidth == 0) {
            break
        }
    }

    val heightIndex = numberGenerator.getInt(index, 0, maxWidthPerRow.size)
    val widthIndex = numberGenerator.getInt(index+1, maxWidthPerRow[heightIndex])

    builder.addBigBrick(x, y, grammar.brick, widthIndex + 1, heightIndex + 1)
}

private fun checkRow(
    maxWidth: Int,
    builder: SimpleBrickMapBuilder,
    x: Int,
    y: Int,
    offsetY: Int,
): Int {
    for (offsetX in 0..<maxWidth) {
        if (builder.getBrick(x + offsetX, y + offsetY).isFull()) {
            return offsetX
        }
    }

    return maxWidth
}

