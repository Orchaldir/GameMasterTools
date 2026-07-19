package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.core.model.visualization.BrickSelectionWithCenter
import at.orchaldir.gm.core.model.visualization.UniformBricks
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders
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
        BrickPattern.Grid -> createGridPattern(builder, grammar.bricks)
        BrickPattern.Herringbone -> createHerringbone(builder, grammar)
        BrickPattern.Pinwheel -> createPinwheelPattern(builder, grammar)
        BrickPattern.PinwheelSplit -> createSplitPinwheelPattern(builder, grammar)
        BrickPattern.PinwheelWithBigCenter -> createPinwheelWithBigCenterPattern(builder, grammar)
        BrickPattern.PythagoreanTiling -> createPythagoreanTiling(builder, grammar)
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
            grammar.bricks,
            basketWeavePosition,
            n,
        )
        sub.addHorizontalBrick(start.x, brickY, 0, grammar.bricks, n)
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
                grammar.bricks,
                start,
                n,
            )
        } else {
            addVerticalBasketWeaveN(
                sub,
                grammar.bricks,
                start,
                n,
            )
        }

        index += n
    }
}

private fun createGridPattern(
    builder: BrickMapBuilder,
    bricks: BrickSelection,
) {
    repeat(builder.size().height) { y ->
        repeat(builder.size().width) { x ->
            builder.addSingleBlock(x, y, bricks)
        }
    }
}

private fun createHerringbone(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val n = grammar.length
    val types = n * 2
    val offsetX = builder.borders().calculateTileOffsetX(builder.size().width, types)
    val offsetY = builder.borders().calculateTileOffsetY(builder.size().height, types)

    repeat(builder.size().height) { y ->
        var x = 0
        var type = (types - y - offsetX + offsetY).mod(types)

        while (x < builder.size().width) {
            when (type) {
                in 0..<n -> {
                    // a horizontal brick

                    if (x == 0 && type > 0) {
                        val remainingLength = n - type

                        if (builder.borders().left) {
                            builder.addHorizontalBrick(0, y, y, grammar.bricks, remainingLength)
                        }

                        x = remainingLength
                    } else {
                        builder.addHorizontalBrick(x, y, y, grammar.bricks, n)

                        x += n
                    }

                    type = n

                    continue
                }

                in n..<types - 1 -> {
                    // vertical brick that started in a row above

                    if (y == 0 && builder.borders().top) {
                        val remainingLength = type - n + 1

                        builder.addVerticalBrick(x, y, x, grammar.bricks, remainingLength)
                    }

                    x++
                }

                types - 1 -> {
                    // vertical brick

                    builder.addVerticalBrick(x, y, x, grammar.bricks, n)

                    x++
                }

                else -> error("Unsupported type $type!")
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
        grammar.bricks,
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
        grammar.bricks,
        grammar.length,
        offset,
        { _ -> 0 },
    )
}

private fun createPinwheelPattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val long = grammar.length
    val short = grammar.length  - 1
    val (center, border) = grammar.bricks.getCenterAndBorderSelection()

    builder.createSubSections(MapSize2d.square(2 * long - 1)) { sub, start ->
        // border
        sub.addBigBrick(start.x + short, start.y, 0, border, long, short)
        sub.addBigBrick(start.x + long, start.y + short, 0, border, short, long)
        sub.addBigBrick(start.x, start.y + long, 1, border, long, short)
        sub.addBigBrick(start.x, start.y, 1, border, short, long)

        // center
        sub.addHorizontalBrick(start.x + short, start.y + short, 0, center, 1)
    }
}

private fun createSplitPinwheelPattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val length = grammar.length
    val rows = grammar.length  - 1
    val (center, border) = grammar.bricks.getCenterAndBorderSelection()

    builder.createSubSections(MapSize2d.square(2 * length - 1)) { sub, start ->
        // border
        sub.addHorizontalBricks(start.x + rows, start.y, border, length, rows)
        sub.addVerticalBricks(start.x + length, start.y + rows, border, length, rows)
        sub.addHorizontalBricks(start.x, start.y + length, border, length, rows)
        sub.addVerticalBricks(start.x, start.y, border, length, rows)

        // center
        sub.addHorizontalBrick(start.x + rows, start.y + rows, 0, center, 1)
    }
}

private fun createPinwheelWithBigCenterPattern(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val length = grammar.length
    val centerSize = grammar.length  - 1
    val (center, border) = grammar.bricks.getCenterAndBorderSelection()

    builder.createSubSections(MapSize2d.square(length + 1)) { sub, start ->
        // border
        sub.addHorizontalBrick(start.x + 1, start.y, 0, border, length)
        sub.addVerticalBrick(start.x + length, start.y + 1, 1, border, length)
        sub.addHorizontalBrick(start.x, start.y + length, 1, border, length)
        sub.addVerticalBrick(start.x, start.y, 0, border, length)

        // center
        sub.addBigBrick(start.x + 1, start.y + 1, 0, center, centerSize, centerSize)
    }
}

private fun createPythagoreanTiling(
    builder: BrickMapBuilder,
    grammar: BrickPatternGrammar,
) {
    val n = grammar.length
    val types = n * n + 1
    val (center, border) = grammar.bricks.getCenterAndBorderSelection()
    val offsetX = builder.borders().calculateTileOffsetX(builder.size().width, types)
    val offsetY = builder.borders().calculateTileOffsetY(builder.size().height, types)
    logger.info { "n=$n types=$types offsetX=$offsetX offsetY=$offsetY" }

    repeat(builder.size().height) { y ->
        var x = 0
        var type = (types + y * n - offsetX - offsetY * n).mod(types)
        logger.info { "y=$y type=$type" }

        while (x < builder.size().width) {
            logger.info { "x=$x type=$type" }
            if (type == 0) {
                // center
                builder.addHorizontalBrick(x, y, 0, center, 1)

                x++
                type++
                continue
            }

            for(i in 0..<n) {
                val start = 1 + i * n
                val end = (1 + i) * n

                if (type in start..end) {
                    val width = end - type + 1
                    val height = n - i
                    val isLeftBorder = x == 0 && builder.borders().left && type > start && i == 0
                    val isTopBorder = y == 0 && builder.borders().top && type == start
                    val isFull = i == 0 && type == start
                    logger.info { "isFull=$isFull" }

                    if (isFull) {
                        builder.addBigBrick(x, y, x, border, n, n)
                    }
                    else if (isLeftBorder || isTopBorder) {
                        val startX = x + width - n
                        builder.addBigBrick(startX, y + height - n, startX, border, n, n)
                    }

                    x += width
                    type += width
                    break
                }
            }

            type %= types
        }
    }
}