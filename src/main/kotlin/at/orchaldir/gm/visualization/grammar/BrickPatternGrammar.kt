package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import kotlin.math.ceil
import kotlin.math.floor

fun visualizeBrickPatternGrammar(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    layer: Int,
) = when (grammar.pattern) {
    BrickPattern.BasketWeaveSingle -> visualizeBasketWeaveSingle(state, grammar, aabb, layer, grammar.length)
    BrickPattern.BasketWeave -> visualizeBasketWeaveN(state, grammar, aabb, layer, grammar.length)
    BrickPattern.Grid -> visualizeGrid(state, grammar, aabb, layer)
    BrickPattern.Herringbone -> visualizeHerringbone(state, grammar, aabb, layer, grammar.length)
    BrickPattern.Running -> visualizeRows(
        state,
        grammar,
        aabb,
        layer,
    ) { x, y ->
        if (x == 0 && y % 2 == 0) {
            floor(grammar.length / 2.0).toInt()
        } else {
            grammar.length
        }
    }

    BrickPattern.Stack -> visualizeRows(
        state,
        grammar,
        aabb,
        layer,
        { _, _ -> grammar.length },
    )
}

private fun visualizeBasketWeaveSingle(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    layer: Int,
    n: Int,
) {
    var index = 0

    visualizeSubSections(
        grammar.size,
        MapSize2d(n, n + 1),
        aabb,
    ) { subSectionX, subSectionY, gridStart, blockSize, limits ->
        val startX = subSectionX * n
        val startY = subSectionY * (n + 1)

        when {
            subSectionX % 2 == 0 -> 0
            startY < limits.height - 1 -> 1
            else -> null
        }?.let { offset ->
            visualizeVerticalBasketWeaveN(
                state,
                grammar.brick,
                gridStart,
                blockSize,
                startX,
                startY + offset,
                limits,
                layer,
                n,
                index,
            )

            index += n
        }

        when {
            subSectionX % 2 == 1 -> 0
            startY < limits.height - 2 -> n
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
                limits,
                layer,
            )
        }
    }
}

private fun visualizeBasketWeaveN(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
    layer: Int,
    n: Int,
) {
    var index = 0

    visualizeSubSections(
        grammar.size,
        MapSize2d.square(n),
        aabb,
    ) { subSectionX, subSectionY, gridStart, blockSize, limits ->
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
                limits,
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
                limits,
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
                layer,
            )
        }
    }
}

private fun visualizeGrid(
    state: GrammarRenderState,
    grammar: BrickPatternGrammar,
    aabb: AABB,
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
    layer: Int,
    length: Int,
) = grammar.size.process(aabb) { gridStart, gridSize, blockSize ->
    val doubleLength = length * 2
    val horizontalBlocks = MapSize2d(length, 1)
    val verticalBlocks = MapSize2d(1, length)
    var index = 0

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
                    gridSize,
                    layer,
                )
            } else if (x > -length) {
                visualizeShapeGrammar(
                    state.addSeed(index++),
                    grammar.brick,
                    gridStart,
                    blockSize,
                    0,
                    y,
                    MapSize2d(length + x, 1),
                    gridSize,
                    layer,
                )
            }

            if (y == 0) {
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
                        gridSize,
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
                    gridSize,
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
    layer: Int,
    calculateLength: (Int, Int) -> Int,
) = grammar.size.process(aabb) { start, gridSize, blockSize ->
    var startOfRow = start
    var index = 0

    repeat(gridSize.height) { y ->
        var currentBrick = startOfRow
        var x = 0

        while (x < gridSize.width) {
            val length = calculateLength(x, y).coerceAtMost(gridSize.width - x)
            val brickSize = blockSize.replaceWidth(Factor.fromNumber(length))

            visualizeShapeGrammar(
                state.addSeed(index),
                grammar.brick,
                AABB(currentBrick, brickSize),
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
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