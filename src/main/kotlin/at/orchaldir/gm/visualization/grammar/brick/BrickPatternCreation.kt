package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders
import kotlin.math.floor

fun createBrickPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> {
    val gridSize = grammar.size.size()
    val builder = BrickMapBuilder(gridSize, borders)

    when (grammar.pattern) {
        BrickPattern.Grid -> createGridPattern(builder, grammar.brick)
        BrickPattern.Running -> createRunningPattern(builder, grammar)
        BrickPattern.Stack -> createStackPattern(builder, grammar)
        else -> return createBrickPatternOld(grammar, borders)
    }

    return builder.finish()
}

private fun createBrickPatternOld(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> = when (grammar.pattern) {
    BrickPattern.BasketWeave -> createBasketWeavePattern(grammar, borders)
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
    ) { grid, subSectionX, subSectionY, gridSize, limitedGridSize, subBorders, offset ->
        val x = subSectionX * n
        val y = subSectionY * n

        val indexForEven = (subSectionX + evenOffsetX + subSectionY + evenOffsetY) % 2
        val isEven = indexForEven == 0

        logger.info { "subSectionX=$subSectionX subSectionY=$subSectionY x=$x y=$y indexForEven=$indexForEven" }
        logger.info { "subBorders=$subBorders offset=$offset" }

        if (isEven) {
            addHorizontalBasketWeaveN(
                grid,
                grammar.brick,
                x,
                y,
                gridSize,
                limitedGridSize,
                subBorders,
                offset,
                n,
            )
        } else {
            addVerticalBasketWeaveN(
                grid,
                grammar.brick,
                x,
                y,
                gridSize,
                limitedGridSize,
                subBorders,
                offset,
                n,
            )
        }

        index += n
    }
}

private fun createGridPattern(
    builder: BrickMapBuilder,
    brick: ShapeGrammar,
) {
    repeat(builder.size().height) { y ->
        repeat(builder.size().width) { x ->
            builder.addSingleBlock(x, y, brick)
        }
    }
}

private fun createRunningPattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val offset = -builder.borders().calculateTileOffsetX2(grammar.size, grammar.length)
    val halfBrick = floor(grammar.length / 2.0).toInt()

    createRowPattern(
        builder,
        grammar.brick,
        grammar.length,
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
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val offset = -builder.borders().calculateTileOffsetX2(grammar.size, grammar.length)

    createRowPattern(
        builder,
        grammar.brick,
        grammar.length,
        offset,
        { _ -> 0 },
    )
}
