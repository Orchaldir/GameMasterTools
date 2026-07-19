package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapPoint2d
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders
import kotlin.math.ceil

private val BLOCK_SIZE = MapSize2d.square(1)

data class Brick(
    val grammar: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d = MapSize2d(1, 1),
) {
    override fun toString() = size.format()
}

abstract class BrickMapBuilder(
    protected val size: MapSize2d,
    protected val borders: Borders,
    protected val map: MutableList<Brick?>,
) {

    abstract fun size(): MapSize2d
    fun borders() = borders

    // doesn't work with createSubSections()
    fun addSingleBlock(x: Int, y: Int, bricks: BrickSelection) {
        val mapIndex = size.toIndexRisky(x, y)
        logger.info { "x=$x y=$y index=$mapIndex" }
        val selected = bricks.select(y, true)

        map[mapIndex] = Brick(selected, BLOCK_SIZE)
    }

    fun addHorizontalBrick(
        x: Int,
        y: Int,
        row: Int,
        bricks: BrickSelection,
        length: Int,
    ) {
        val (limitedX, limitedLength) = applyBorders(
            borders.left,
            borders.right,
            size().width,
            x,
            length,
        ) ?: return

        addBrick(limitedX, y, row, bricks, MapSize2d(limitedLength, 1), true)
    }

    fun addHorizontalBricks(
        x: Int,
        y: Int,
        bricks: BrickSelection,
        length: Int,
        n: Int,
    ) {
        repeat(n) { i ->
            val row = y + i
            addHorizontalBrick(x, row, row, bricks, length)
        }
    }

    fun addVerticalBrick(
        x: Int,
        y: Int,
        row: Int,
        bricks: BrickSelection,
        length: Int,
    ) {
        val (limitedY, limitedLength) = applyBorders(
            borders.top,
            borders.bottom,
            size().height,
            y,
            length,
        ) ?: return

        addBrick(x, limitedY, row, bricks, MapSize2d(1, limitedLength), false)
    }

    fun addVerticalBricks(
        x: Int,
        y: Int,
        bricks: BrickSelection,
        length: Int,
        n: Int,
    ) {
        repeat(n) { i ->
            val row = x + i
            addVerticalBrick(row, y, row, bricks, length)
        }
    }

    fun addBigBrick(
        x: Int,
        y: Int,
        row: Int,
        bricks: BrickSelection,
        width: Int,
        height: Int,
    ) = addBigBrick(x, y, row, bricks, width, height, width >= height)

    fun addBigBrick(
        x: Int,
        y: Int,
        row: Int,
        bricks: BrickSelection,
        width: Int,
        height: Int,
        isHorizontal: Boolean,
    ) {
        val (limitedX, limitedWidth) = applyBorders(
            borders.left,
            borders.right,
            size().width,
            x,
            width,
        ) ?: return
        val (limitedY, limitedHeight) = applyBorders(
            borders.top,
            borders.bottom,
            size().height,
            y,
            height,
        ) ?: return

        addBrick(limitedX, limitedY, row, bricks, MapSize2d(limitedWidth, limitedHeight), isHorizontal)
    }

    protected fun addBrick(
        x: Int,
        y: Int,
        row: Int,
        bricks: BrickSelection,
        size: MapSize2d,
        isHorizontal: Boolean,
    ) {
        val selected = bricks.select(row, isHorizontal)

        addBrick(x, y, Brick(selected, size))
    }

    protected abstract fun addBrick(x: Int, y: Int, brick: Brick)

    fun createSubSections(
        subSize: MapSize2d,
        addSubSection: (SubSectionBuilder, MapPoint2d) -> Unit,
    ) {
        val offset = borders.calculateTileOffset(size(), subSize)
        val subSections = MapSize2d(
            ceil((size().width - offset.x) / subSize.width.toDouble()).toInt(),
            ceil((size().height - offset.y) / subSize.height.toDouble()).toInt(),
        )

        // render subsections for the top border
        repeat(subSections.width) { subSectionX ->
            addSubSection(
                Borders(top = borders.top, tileX = subSectionX, tileY = -1),
                subSize,
                MapPoint2d(
                    offset.x,
                    offset.y - subSize.height,
                ),
                { _ -> MapPoint2d(subSize.width * subSectionX, 0) },
                addSubSection,
            )
        }

        // render subsections for the left border
        repeat(subSections.height) { subSectionY ->
            addSubSection(
                Borders(left = borders.left, tileX = -1, tileY = subSectionY),
                subSize,
                MapPoint2d(
                    offset.x - subSize.width,
                    offset.y,
                ),
                { _ -> MapPoint2d(0, subSize.height * subSectionY) },
                addSubSection,
            )
        }

        // render subsections
        repeat(subSections.height) { subSectionY ->
            repeat(subSections.width) { subSectionX ->
                addSubSection(
                    calculateSubBorders(borders, subSections, subSectionX, subSectionY),
                    subSize,
                    MapPoint2d(),
                    { subStart -> subStart + offset },
                    addSubSection,
                )
            }
        }
    }

    private fun addSubSection(
        subBorders: Borders,
        subSize: MapSize2d,
        addOffset: MapPoint2d,
        calculateBuilderOffset: (MapPoint2d) -> MapPoint2d,
        addSubSection: (SubSectionBuilder, MapPoint2d) -> Unit,
    ) {
        val subIndex = MapPoint2d(subBorders.tileX, subBorders.tileY)
        val subStart = subIndex * subSize
        val bOffset = calculateBuilderOffset(subStart)
        val limitedSubSize = limitSubSize(bOffset, subSize)
        val subSection = SubSectionBuilder(
            size,
            subBorders,
            map,
            subIndex,
            limitedSubSize,
            bOffset,
        )

        addSubSection(subSection, addOffset)
    }

    private fun limitSubSize(subStart: MapPoint2d, subSize: MapSize2d): MapSize2d {
        val subEnd = subStart + subSize

        return MapSize2d(
            limitSubSize(subEnd.x, subSize.width, size.width),
            limitSubSize(subEnd.y, subSize.height, size.height),
        )
    }

    private fun limitSubSize(subEnd: Int, subSize: Int, size: Int) = if (subEnd > size) {
        subSize - (subEnd - size)
    } else {
        subSize
    }

    fun finish() = TileMap2d(size, map)
}

class SimpleBrickMapBuilder(
    size: MapSize2d,
    borders: Borders,
    map: MutableList<Brick?>,
) : BrickMapBuilder(size, borders, map) {

    constructor(size: MapSize2d, borders: Borders) :
            this(size, borders, MutableList<Brick?>(size.tiles()) { null })

    override fun size() = size

    override fun addBrick(x: Int, y: Int, brick: Brick) {
        val mapIndex = size.toIndexRisky(x, y)

        map[mapIndex] = brick
    }
}

class SubSectionBuilder(
    size: MapSize2d,
    borders: Borders,
    map: MutableList<Brick?>,
    val subIndex: MapPoint2d,
    private val subSize: MapSize2d,
    private val offset: MapPoint2d,
) : BrickMapBuilder(size, borders, map) {

    override fun size() = subSize

    override fun addBrick(x: Int, y: Int, brick: Brick) {
        size.toIndex(offset.x + x, offset.y + y)?.let { mapIndex ->
            map[mapIndex] = brick
        }
    }
}

private fun calculateSubBorders(
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

private fun applyBorders(
    startBorder: Boolean,
    endBorder: Boolean,
    gridSize: Int,
    position: Int,
    length: Int,
): Pair<Int, Int>? {
    var limitedPosition = position
    var limitedLength = length

    if (position < 0) {
        val remainingLength = length + position

        if (startBorder && remainingLength > 0) {
            limitedPosition = 0
            limitedLength = remainingLength
        } else {
            return null
        }
    } else if (endBorder && position + length > gridSize) {
        val maxLength = gridSize - position

        limitedLength = length.coerceAtMost(maxLength)
    }

    return Pair(limitedPosition, limitedLength)
}