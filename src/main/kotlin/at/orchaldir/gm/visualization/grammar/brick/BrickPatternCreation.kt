package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.BrickSelection
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
        BrickPattern.Running -> createRunningPattern(builder, grammar)
        BrickPattern.Stack -> createStackPattern(builder, grammar)
        BrickPattern.Pinwheel -> createPinwheelPattern(builder, grammar)
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

    builder.createSubSections(MapSize2d.square(2 * long - 1)) { sub, start ->
        sub.addBigBrick(start.x + short, start.y, 0, grammar.bricks, long, short)
        sub.addBigBrick(start.x + long, start.y + short, 0, grammar.bricks, short, long)
        sub.addBigBrick(start.x, start.y + long, 1, grammar.bricks, long, short)
        sub.addBigBrick(start.x, start.y, 1, grammar.bricks, short, long)
        sub.addHorizontalBrick(start.x + short, start.y + short, 0, grammar.bricks, 1, 1)
    }
}