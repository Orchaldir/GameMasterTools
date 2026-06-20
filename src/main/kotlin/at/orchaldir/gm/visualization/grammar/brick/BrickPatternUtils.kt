package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapPoint2d

fun createRowPattern(
    builder: BrickMapBuilder,
    brick: ShapeGrammar,
    length: Int,
    offset: Int,
    calculateStartOfRow: (Int) -> Int,
) {
    repeat(builder.size().height) { y ->
        var x = calculateStartOfRow(y) + offset

        while (x < builder.size().width) {
            builder.addHorizontalBrick(x, y, brick, length)

            x += length
        }
    }
}

fun addHorizontalBasketWeaveN(
    builder: SubSectionBuilder,
    brick: ShapeGrammar,
    start: MapPoint2d,
    n: Int,
) {
    repeat(n) { i ->
        builder.addHorizontalBrick(start.x, start.y + i, brick, n)
    }
}

fun addVerticalBasketWeaveN(
    builder: SubSectionBuilder,
    brick: ShapeGrammar,
    start: MapPoint2d,
    n: Int,
) {
    repeat(n) { i ->
        builder.addVerticalBrick(start.x + i, start.y, brick, n)
    }
}