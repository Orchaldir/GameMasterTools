package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.modulo
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

fun visualizeBrickPatternGrammar(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
) = when (grammar.pattern) {
    BrickPattern.BasketWeaveSingle -> visualizeBasketWeaveSingle(
        state,
        grammar,
        aabb,
        borders,
        layer,
        grammar.length,
    )
    BrickPattern.BasketWeave -> visualizeBasketWeaveN(
        state,
        grammar,
        aabb,
        borders,
        layer,
        grammar.length,
    )
    BrickPattern.Grid -> visualizeGrid(state, grammar, aabb, borders, layer)
    BrickPattern.Herringbone -> visualizeHerringbone(state, grammar, aabb, borders, layer, grammar.length)
    BrickPattern.Running -> {
        val offset = borders.calculateTileOffsetX(grammar.size, grammar.length)
        val halfBrick = floor(grammar.length / 2.0).toInt()

        logger.info { "offset=$offset halfBrick=$halfBrick" }

        visualizeRows(
            state,
            grammar,
            aabb,
            borders,
            offset,
            layer,
        ) { y ->
            if (y % 2 == 0) {
                -halfBrick
            } else {
                0
            }
        }
    }

    BrickPattern.Stack -> {
        val offset = borders.calculateTileOffsetX(grammar.size, grammar.length)

        visualizeRows(
            state,
            grammar,
            aabb,
            borders,
            offset,
            layer,
            { _ -> 0 },
        )
    }
}

private fun visualizeBasketWeaveSingle(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
    n: Int,
) {
    var index = 0
    val evenOffset = borders.calculateEvenOffsetX(grammar.size, n)

    visualizeSubSections(
        grammar.size,
        MapSize2d(n, n + 1),
        aabb,
        borders,
    ) { subSectionX, subSectionY, gridStart, blockSize, gridSize, _ ->
        val startX = subSectionX * n
        val startY = subSectionY * (n + 1)
        val isEven = (subSectionX + evenOffset) % 2 == 0

        when {
            isEven -> 0
            !borders.bottom || startY < gridSize.height - 1 -> 1
            else -> null
        }?.let { offset ->
            visualizeVerticalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                startX,
                startY + offset,
                borders.applyBottomAndRight(gridSize),
                borders,
                layer,
                n,
                index,
            )

            index += n
        }

        when {
            !isEven -> 0
            !borders.bottom || startY < gridSize.height - 2 -> n
            else -> null
        }?.let { offset ->
            visualizeShapeGrammar(
                state.addSeed(index++),
                grammar.brick,
                gridStart,
                blockSize,
                startX,
                startY + offset,
                MapSize2d(n, 1),
                borders.applyBottomAndRight(gridSize),
                borders,
                layer,
            )
        }
    }
}

private fun visualizeBasketWeaveN(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
    n: Int,
) {
    var index = 0
    val evenOffsetX = borders.calculateEvenOffsetX(grammar.size, n)
    val evenOffsetY = borders.calculateEvenOffsetY(grammar.size, n)

    visualizeSubSections(
        grammar.size,
        MapSize2d.square(n),
        aabb,
        borders,
    ) { subSectionX, subSectionY, gridStart, blockSize, gridSize, _ ->
        val x = subSectionX * n
        val y = subSectionY * n
        val isEven = (subSectionX + evenOffsetX + subSectionY + evenOffsetY) % 2

        if (isEven == 0) {
            visualizeHorizontalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                x,
                y,
                borders.applyRight(gridSize),
                borders,
                layer,
                n,
                index,
            )
        } else {
            visualizeVerticalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                x,
                y,
                borders.applyBottom(gridSize),
                borders,
                layer,
                n,
                index,
            )
        }

        index += n
    }
}

private fun visualizeHorizontalBasketWeaveN(
    state: GrammarRenderState,
    grammar: ShapeGrammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    limits: MapSize2d,
    borders: Borders,
    layer: Int,
    n: Int,
    startIndex: Int,
) {
    val blocks = MapSize2d(n, 1)
    var index = startIndex

    repeat(n) { offset ->
        val currentY = y + offset

        if (currentY < limits.height) {
            visualizeShapeGrammar(
                state.addSeed(index++),
                grammar,
                gridStart,
                blockSize,
                x,
                currentY,
                blocks,
                limits,
                borders,
                layer,
            )
        }
    }
}

private fun visualizeVerticalBasketWeaveN(
    state: GrammarRenderState,
    grammar: ShapeGrammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    limits: MapSize2d,
    borders: Borders,
    layer: Int,
    n: Int,
    startIndex: Int,
) {
    val blocks = MapSize2d(1, n)
    var index = startIndex

    repeat(n) { offset ->
        val currentX = x + offset

        if (!borders.right || currentX < limits.width) {
            visualizeShapeGrammar(
                state.addSeed(index++),
                grammar,
                gridStart,
                blockSize,
                currentX,
                y,
                blocks,
                limits,
                borders,
                layer,
            )
        }
    }
}

private fun visualizeGrid(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
) = grammar.size.process(aabb) { start, gridSize, brickSize ->
    var startOfRow = start
    var index = 0

    repeat(gridSize.height) {
        var currentBrick = startOfRow

        repeat(gridSize.width) {
            visualizeShapeGrammar(
                state.addSeed(index),
                grammar.brick,
                AABB(currentBrick, brickSize),
                borders,
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
            index++
        }

        startOfRow = startOfRow.addHeight(brickSize.height)
    }
}

private fun visualizeHerringbone(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
    length: Int,
) {
    val doubleLength = length * 2
    val types = length + 1
    val singleBlock = MapSize2d.square(1)
    val horizontalBlocks = MapSize2d(length, 1)
    val verticalBlocks = MapSize2d(1, length)
    val limits = borders.apply(grammar.size.size())
    var index = 0

    visualizeSubSections(
        grammar.size,
        MapSize2d.square(doubleLength),
        aabb,
        borders,
    ) { subSectionX, subSectionY, gridStart, blockSize, gridSize, subBorders ->
        val startX = subSectionX * doubleLength
        val startY = subSectionY * doubleLength
        val remainingWidth = gridSize.width - startX
        val remainingHeight = gridSize.height - startY
        val width = if (subBorders.right) {
            doubleLength.coerceAtMost(remainingWidth)
        } else {
            doubleLength
        }
        val height = if (subBorders.bottom) {
            doubleLength.coerceAtMost(remainingHeight)
        } else {
            doubleLength
        }

        logger.info { "subSectionX=$subSectionX subSectionY=$subSectionY remainingWidth=$remainingWidth width=$width remainingHeight=$remainingHeight height=$height" }
        logger.info { "subBorders=$subBorders" }

        repeat(height) { y ->
            var x = 0
            var type = (types - y) % types

            logger.info { "y=$y types=$types type=$type" }

            while (x < width) {
                logger.info { "x=$x type=$type" }

                if (type == -1 && subSectionX == 0 && subSectionY == 0) {
                    logger.info { "debug" }
                }

                val brick = if (type <= 0) {
                    // horizontal brick
                    val isBrickSharedLeft = x == 0 && y > 0
                    val isBrickSharedRight = x == doubleLength - 1
                    val oldType = type
                    type = 0

                    if (isBrickSharedLeft && subBorders.left) {
                        MapSize2d(y - length, 1)
                    } else if (isBrickSharedRight && subBorders.right) {
                        singleBlock
                    } else if (isBrickSharedLeft) {
                        x += 1 + oldType.absoluteValue
                        type = 1
                        continue
                    } else {
                        horizontalBlocks
                    }
                } else if (type < length) {
                    // vertical brick that started in a row above

                    if (subBorders.top && y == 0) {
                        MapSize2d(1, x - length + 1)
                    } else {
                        x++
                        type++
                        continue
                    }
                } else {
                    // vertical brick

                    if (subBorders.bottom && y > length) {
                        MapSize2d(1, doubleLength - y)
                    } else {
                        verticalBlocks
                    }
                }

                visualizeShapeGrammar(
                    state.addSeed(index++),
                    grammar.brick,
                    gridStart,
                    blockSize,
                    startX + x,
                    startY + y,
                    brick,
                    limits,
                    borders,
                    layer,
                )

                x += brick.width
                type = (type + 1) % types
            }
        }
    }
}

private fun visualizeRows(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    offset: Int,
    layer: Int,
    calculateStartX: (Int) -> Int,
) = grammar.size.process(aabb) { start, gridSize, blockSize ->
    var startOfRow = start
    var index = 0

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

            visualizeShapeGrammar(
                state.addSeed(index++),
                grammar.brick,
                start,
                blockSize,
                 x + offset,
                y,
                MapSize2d(length, 1),
                borders.applyBottomAndRight(gridSize),
                borders,
                layer,
            )

            x += length
            index++
        }

        startOfRow = startOfRow.addHeight(blockSize.height)
    }
}

private fun visualizeSubSections(
    gridSize: GridSize,
    subSectionSize: MapSize2d,
    aabb: AABB,
    borders: Borders,
    visualizeSubSection: (Int, Int, Point2d, Size2d, MapSize2d, Borders) -> Unit,
) = gridSize.process(aabb) { start, gridSize, blockSize ->
    val offset = borders.calculateTileOffset(gridSize, subSectionSize)
    val startWithOffset = start + blockSize * offset
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
                if (x  == 0) {
                    borders.left
                } else {
                    false
                },
                if (x < subSections.width - 1) {
                    false
                } else {
                    borders.right
                },
                if (y  == 0) {
                    borders.top
                } else {
                    false
                },
                x,
                y,
            )
            visualizeSubSection(
                x,
                y,
                startWithOffset,
                blockSize,
                gridSizeWithOffset,
                subBorders,
            )
        }
    }
}