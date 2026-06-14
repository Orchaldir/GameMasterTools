package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapPoint2d
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders
import kotlin.math.ceil

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

fun createSubSections(
    grammarSize: GridSize,
    subSectionSize: MapSize2d,
    borders: Borders,
    addSubSection: (MutableList<Brick?>, Int, Int, MapSize2d, MapSize2d, Borders, MapPoint2d) -> Unit,
): TileMap2d<Brick?> {
    val gridSize = grammarSize.size()
    val grid = MutableList<Brick?>(gridSize.tiles()) { null }
    val offset = borders.calculateTileOffset2(gridSize, subSectionSize)
    val leftOffset = MapPoint2d(
        offset.x - subSectionSize.width,
        offset.y,
    )
    val limitedGridSize = gridSize - offset
    val subSections = MapSize2d(
        ceil((gridSize.width - offset.x) / subSectionSize.width.toDouble()).toInt(),
        ceil((gridSize.height - offset.y) / subSectionSize.height.toDouble()).toInt(),
    )

    repeat(subSections.height) { subSectionY ->
        /*
        if (borders.left) {
            val subBorders = Borders(false, -1, subSectionY)

            addSubSection(
                grid,
                -1,
                subSectionY,
                gridSize,
                limitedGridSize,
                subBorders,
                offset,
            )
        }
        */

        repeat(subSections.width) { subSectionX ->
            val subBorders = calculateSubBorders(borders, subSections, subSectionX, subSectionY)

            addSubSection(
                grid,
                subSectionX,
                subSectionY,
                gridSize,
                limitedGridSize,
                subBorders,
                offset,
            )
        }
    }

    return TileMap2d(gridSize, grid)
}

fun calculateSubBorders(
    borders: Borders,
    subSections: MapSize2d,
    subSectionX: Int,
    subSectionY: Int,
) = Borders(
    if (subSectionY < subSections.height - 1) {
        false
    } else {
        borders.bottom
    },
    if (subSectionX == 0) {
        borders.left
    } else {
        false
    },
    if (subSectionX < subSections.width - 1) {
        false
    } else {
        borders.right
    },
    if (subSectionY == 0) {
        borders.top
    } else {
        false
    },
    subSectionX,
    subSectionY,
)

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