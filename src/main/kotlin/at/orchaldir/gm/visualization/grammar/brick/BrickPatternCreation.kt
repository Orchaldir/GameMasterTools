package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders
import kotlin.math.absoluteValue
import kotlin.math.floor

fun createBrickPattern(
    grammar: BrickPatternGrammar,
    borders: Borders,
): TileMap2d<Brick?> {
    val gridSize = grammar.size.size()
    val builder = SimpleBrickMapBuilder(gridSize, borders)

    when (grammar.pattern) {
        BrickPattern.BasketWeaveSingle -> createBasketWeaveSinglePattern(builder, grammar)
        BrickPattern.BasketWeave -> createBasketWeavePattern(builder, grammar)
        BrickPattern.Grid -> createGridPattern(builder, grammar.brick)
        BrickPattern.Herringbone -> createHerringbone(builder, grammar)
        BrickPattern.Running -> createRunningPattern(builder, grammar)
        BrickPattern.Stack -> createStackPattern(builder, grammar)
    }

    return builder.finish()
}

private fun createBasketWeaveSinglePattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val n = grammar.length
    val evenOffset = builder.borders().calculateEvenOffsetX(grammar.size, n)

    builder.createSubSections(MapSize2d(n, n + 1)) { sub, start ->
        val indexForEven = (sub.subIndex.x + evenOffset) % 2
        val isEven = indexForEven == 0
        val basketWeavePosition = if (isEven) {
            start
        } else {
            start.modifyY(1)
        }
        val brickY = if (isEven) {
            start.y + n
        } else {
            start.y
        }

        addVerticalBasketWeaveN(
            sub,
            grammar.brick,
            basketWeavePosition,
            n,
        )
        sub.addHorizontalBrick(start.x, brickY, grammar.brick, n)
    }
}

private fun createBasketWeavePattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    var index = 0
    val n = grammar.length
    val evenOffsetX = builder.borders().calculateEvenOffsetX(grammar.size, n)
    val evenOffsetY = builder.borders().calculateEvenOffsetY(grammar.size, n)

    builder.createSubSections(MapSize2d.square(n)) { sub, start ->
        val indexForEven = (sub.subIndex.x + evenOffsetX + sub.subIndex.y + evenOffsetY) % 2
        val isEven = indexForEven == 0

        if (isEven) {
            addHorizontalBasketWeaveN(
                sub,
                grammar.brick,
                start,
                n,
            )
        } else {
            addVerticalBasketWeaveN(
                sub,
                grammar.brick,
                start,
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

private fun createHerringbone(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val n = grammar.length
    val offset = builder.borders().calculateTileOffsetX(builder.size().width, n * 2)
    val types = n + 1

    repeat(builder.size().height) { y ->
        if (y == 2) {
            logger.info { "debug" }
        }

        var x = 0
        var type = (types - y - offset) % types

        logger.info { "y=$y types=$types type=$type" }

        while (x < builder.size().width) {
            logger.info { "x=$x type=$type" }

            if (type <= 0) {
                val isBrickSharedLeft = x == 0 && y > 0

                if (isBrickSharedLeft) {
                    val remainingLength = 1 + type.absoluteValue

                    if (builder.borders().left) {
                        builder.addHorizontalBrick(0, y, grammar.brick, remainingLength)
                    }

                    x = remainingLength
                    type = 1
                    continue
                }
                else {
                    logger.info { "add Horizontal" }
                    builder.addHorizontalBrick(x, y, grammar.brick, n)
                }

                type = 0
                x += n
            } else if (type < n) {
                // vertical brick that started in a row above

                if (y == 0 && builder.borders().top) {
                    builder.addVerticalBrick(x, y, grammar.brick, type)
                }

                logger.info { "skip Vertical" }
                x++
            } else {
                // vertical brick

                builder.addVerticalBrick(x, y, grammar.brick, n)
                logger.info { "add Vertical" }

                x++
            }

            type = (type + 1) % types
        }
    }
}

private fun createRunningPattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val offset = -builder.borders().calculateTileOffsetX(grammar.size, grammar.length)
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
    val offset = -builder.borders().calculateTileOffsetX(grammar.size, grammar.length)

    createRowPattern(
        builder,
        grammar.brick,
        grammar.length,
        offset,
        { _ -> 0 },
    )
}
