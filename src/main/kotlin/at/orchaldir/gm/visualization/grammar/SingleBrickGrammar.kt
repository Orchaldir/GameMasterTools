package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickPattern
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import kotlin.math.ceil

fun visualizeSingleBrickGrammar(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = when (grammar.pattern) {
    SingleBrickPattern.BasketWeaveSingle -> visualizeBasketWeaveSingle(state, grammar, aabb, layer)
    SingleBrickPattern.BasketWeaveDouble -> visualizeBasketWeaveDouble(state, grammar, aabb, layer)
    SingleBrickPattern.Grid -> visualizeGrid(state, grammar, aabb, layer)
    SingleBrickPattern.Herringbone -> visualizeHerringbone(state, grammar, aabb, layer, 2)
    SingleBrickPattern.Running -> visualizeRows(
        state,
        grammar,
        aabb,
        layer,
    ) { x, y ->
        if (x == 0 && y % 2 == 0) {
            1
        } else {
            2
        }
    }

    SingleBrickPattern.Stack -> visualizeRows(
        state,
        grammar,
        aabb,
        layer,
        { _, _ -> 2 },
    )
}

private fun visualizeBasketWeaveSingle(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = visualizeSubSections(
    grammar.size,
    MapSize2d(2, 3),
    aabb,
) { subSectionX, subSectionY, gridStart, blockSize, limits ->
    val startX = subSectionX * 2
    val startY = subSectionY * 3

    when {
        subSectionX % 2 == 0 -> 0
        startY < limits.height - 1 -> 1
        else -> null
    }?.let { offset ->
        visualizeVerticalBasketWeaveDouble(
            state,
            grammar.brick,
            gridStart,
            blockSize,
            startX,
            startY + offset,
            limits,
            layer,
        )
    }

    when {
        subSectionX % 2 == 1 -> 0
        startY < limits.height - 1 -> 2
        else -> null
    }?.let { offset ->
        visualizeGrammar(
            state,
            grammar.brick,
            gridStart,
            blockSize,
            startX,
            startY + offset,
            MapSize2d(2, 1),
            limits,
            layer,
        )
    }
}

private fun visualizeBasketWeaveDouble(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = visualizeSubSections(
    grammar.size,
    MapSize2d.square(2),
    aabb,
) { subSectionX, subSectionY, gridStart, blockSize, limits ->
    val x = subSectionX * 2
    val y = subSectionY * 2

    if ((subSectionX + subSectionY) % 2 == 0) {
        visualizeHorizontalBasketWeaveDouble(state, grammar.brick, gridStart, blockSize, x, y, limits, layer)
    } else {
        visualizeVerticalBasketWeaveDouble(state, grammar.brick, gridStart, blockSize, x, y, limits, layer)
    }
}

private fun visualizeHorizontalBasketWeaveDouble(
    state: GrammarRenderState,
    grammar: Grammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    limits: MapSize2d,
    layer: Int,
) {
    val blocks = MapSize2d(2, 1)

    visualizeGrammar(
        state,
        grammar,
        gridStart,
        blockSize,
        x,
        y,
        blocks,
        limits,
        layer,
    )

    if (y < limits.height - 1) {
        visualizeGrammar(
            state,
            grammar,
            gridStart,
            blockSize,
            x,
            y + 1,
            blocks,
            limits,
            layer,
        )
    }
}

private fun visualizeVerticalBasketWeaveDouble(
    state: GrammarRenderState,
    grammar: Grammar,
    gridStart: Point2d,
    blockSize: Size2d,
    x: Int,
    y: Int,
    limits: MapSize2d,
    layer: Int,
) {
    val blocks = MapSize2d(1, 2)

    visualizeGrammar(
        state,
        grammar,
        gridStart,
        blockSize,
        x,
        y,
        blocks,
        limits,
        layer,
    )

    if (x < limits.width - 1) {
        visualizeGrammar(
            state,
            grammar,
            gridStart,
            blockSize,
            x + 1,
            y,
            blocks,
            limits,
            layer,
        )
    }
}

private fun visualizeGrid(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
) = grammar.size.process(aabb) { start, gridSize, brickSize ->
    var startOfRow = start

    repeat(gridSize.height) {
        var currentBrick = startOfRow

        repeat(gridSize.width) {
            visualizeGrammar(
                state,
                grammar.brick,
                AABB(currentBrick, brickSize),
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
        }

        startOfRow = startOfRow.addHeight(brickSize.height)
    }
}

private fun visualizeHerringbone(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
    length: Int,
) = grammar.size.process(aabb) { gridStart, gridSize, blockSize ->
    val doubleLength = length * 2
    val horizontalBlocks = MapSize2d(length, 1)
    val verticalBlocks = MapSize2d(1, length)

    repeat(gridSize.height) { y ->
        val modulo = y % doubleLength
        var x = if (modulo == 0) { 0 } else { modulo - doubleLength }

        while (x < gridSize.width) {
            if (x >= 0) {
                visualizeGrammar(
                    state,
                    grammar.brick,
                    gridStart,
                    blockSize,
                    x,
                    y,
                    horizontalBlocks,
                    gridSize,
                    layer,
                )
            }

            x += length * 2 - 1

            if (x >= gridSize.width) {
                break
            }

            visualizeGrammar(
                state,
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

private fun visualizeRows(
    state: GrammarRenderState,
    grammar: SingleBrickGrammar,
    aabb: AABB,
    layer: Int,
    calculateLength: (Int, Int) -> Int,
) = grammar.size.process(aabb) { start, gridSize, blockSize ->
    var startOfRow = start

    repeat(gridSize.height) { y ->
        var currentBrick = startOfRow
        var x = 0

        while (x < gridSize.width) {
            val length = calculateLength(x, y).coerceAtMost(gridSize.width - x)
            val brickSize = blockSize.replaceWidth(Factor.fromNumber(length))

            visualizeGrammar(
                state,
                grammar.brick,
                AABB(currentBrick, brickSize),
                layer,
            )

            currentBrick = currentBrick.addWidth(brickSize.width)
            x += length
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