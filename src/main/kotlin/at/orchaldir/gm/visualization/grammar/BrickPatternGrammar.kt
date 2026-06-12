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
        val runningOffset = (offset +  halfBrick).modulo(grammar.length)

        logger.info { "halfBrick=$halfBrick runningOffset=$runningOffset" }

        visualizeRows(
            state,
            grammar,
            aabb,
            borders,
            layer,
        ) { y ->
            if (y % 2 == 0) {
                runningOffset
            } else {
                offset
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
            layer,
            { _ -> offset },
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
    val evenOffset = grammar.size.width() * borders.x / n % 2

    visualizeSubSections(
        grammar.size,
        MapSize2d(n, n + 1),
        aabb,
    ) { subSectionX, subSectionY, gridStart, blockSize, gridSize ->
        val startX = subSectionX * n
        val startY = subSectionY * (n + 1)
        val isEven = (subSectionX + evenOffset) % 2 == 0

        when {
            isEven -> 0
            startY < gridSize.height - 1 -> 1
            else -> null
        }?.let { offset ->
            visualizeVerticalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                startX,
                startY + offset,
                borders.applyBottom(gridSize),
                borders,
                layer,
                n,
                index,
            )

            index += n
        }

        when {
            !isEven -> 0
            startY < gridSize.height - 2 -> n
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
                borders.applyLeft(gridSize),
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

    visualizeSubSections(
        grammar.size,
        MapSize2d.square(n),
        aabb,
    ) { subSectionX, subSectionY, gridStart, blockSize, gridSize ->
        val x = subSectionX * n
        val y = subSectionY * n

        if ((subSectionX + subSectionY) % 2 == 0) {
            visualizeHorizontalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                x,
                y,
                borders.applyLeft(gridSize),
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

        if (currentX < limits.width) {
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
) = grammar.size.process(aabb) { gridStart, gridSize, blockSize ->
    val doubleLength = length * 2
    val horizontalBlocks = MapSize2d(length, 1)
    val verticalBlocks = MapSize2d(1, length)
    var index = 0
    val limits = borders.apply(gridSize)

    repeat(gridSize.height) { y ->
        val modulo = y % doubleLength
        var x = if (modulo == 0) {
            0
        } else {
            modulo - doubleLength
        }

        while (x < gridSize.width) {
            if (x >= 0) {
                visualizeShapeGrammar(
                    state.addSeed(index++),
                    grammar.brick,
                    gridStart,
                    blockSize,
                    x,
                    y,
                    horizontalBlocks,
                    limits,
                    borders,
                    layer,
                )
            } else if (x > -length && borders.left) {
                visualizeShapeGrammar(
                    state.addSeed(index++),
                    grammar.brick,
                    gridStart,
                    blockSize,
                    0,
                    y,
                    MapSize2d(length + x, 1),
                    limits,
                    borders,
                    layer,
                )
            }

            if (y == 0 && borders.top) {
                x += length

                repeat(length.coerceAtMost(gridSize.width - x)) { i ->
                    visualizeShapeGrammar(
                        state.addSeed(index++),
                        grammar.brick,
                        gridStart,
                        blockSize,
                        x,
                        0,
                        MapSize2d(1, 1 + i),
                        limits,
                        borders,
                        layer,
                    )

                    x += 1
                }
            } else {
                x += doubleLength - 1

                if (x >= gridSize.width) {
                    break
                }

                visualizeShapeGrammar(
                    state.addSeed(index++),
                    grammar.brick,
                    gridStart,
                    blockSize,
                    x,
                    y,
                    verticalBlocks,
                    limits,
                    borders,
                    layer,
                )

                x += 1
            }
        }
    }
}

private fun visualizeRows(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    borders: Borders,
    layer: Int,
    calculateStartX: (Int) -> Int,
) = grammar.size.process(aabb) { start, gridSize, blockSize ->
    var startOfRow = start
    var index = 0

    repeat(gridSize.height) { y ->
        var x = calculateStartX(y)
        var currentBrick = startOfRow.addWidth(blockSize.width * x)

        while (x < gridSize.width) {
            val length = if (x < 0) {
                val remainingLength = grammar.length + x

                if (borders.left) {
                    x = 0

                    remainingLength
                } else {
                    x += grammar.length
                    currentBrick = currentBrick.addWidth(blockSize.width * remainingLength)

                    grammar.length
                }
            } else {
                grammar.length
            }
            val coercedLength = if (borders.right) {
                length.coerceAtMost(gridSize.width - x)
            } else {
                length
            }
            val brickSize = blockSize.replaceWidth(Factor.fromNumber(coercedLength))

            visualizeShapeGrammar(
                state.addSeed(index),
                grammar.brick,
                AABB(currentBrick, brickSize),
                borders,
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
            x += coercedLength
            index++
        }

        startOfRow = startOfRow.addHeight(blockSize.height)
    }
}

private fun visualizeSubSections(
    gridSize: GridSize,
    subSectionSize: MapSize2d,
    aabb: AABB,
    visualizeSubSection: (Int, Int, Point2d, Size2d, MapSize2d) -> Unit,
) = gridSize.process(aabb) { start, gridSize, blockSize ->
    val subSections = MapSize2d(
        ceil(gridSize.width / subSectionSize.width.toDouble()).toInt(),
        ceil(gridSize.height / subSectionSize.height.toDouble()).toInt(),
    )

    repeat(subSections.height) { y ->
        repeat(subSections.width) { x ->
            visualizeSubSection(
                x,
                y,
                start,
                blockSize,
                gridSize,
            )
        }
    }
}