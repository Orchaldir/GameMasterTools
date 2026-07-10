package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.utils.map.MapPoint2d

fun createRowPattern(
    builder: BrickMapBuilder,
    bricks: BrickSelection,
    length: Int,
    offset: Int,
    calculateStartOfRow: (Int) -> Int,
) {
    repeat(builder.size().height) { y ->
        var x = calculateStartOfRow(y) + offset

        while (x < builder.size().width) {
            builder.addHorizontalBrick(x, y, y, bricks, length)

            x += length
        }
    }
}

fun addHorizontalBasketWeaveN(
    builder: SubSectionBuilder,
    bricks: BrickSelection,
    start: MapPoint2d,
    n: Int,
) {
    repeat(n) { i ->
        val row = start.y + i
        builder.addHorizontalBrick(start.x, row, row, bricks, n)
    }
}

fun addVerticalBasketWeaveN(
    builder: SubSectionBuilder,
    bricks: BrickSelection,
    start: MapPoint2d,
    n: Int,
) {
    repeat(n) { i ->
        val row = start.x + i
        builder.addVerticalBrick(row, start.y, row, bricks, n)
    }
}