package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapPoint2d
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.grammar.Borders
import kotlin.math.ceil

private val BLOCK_SIZE = MapSize2d.square(1)

data class Brick(
    val grammar: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
) {
    override fun toString() = size.format()
}

open class BrickMapBuilder(
    protected val size: MapSize2d,
    protected val borders: Borders,
    protected val map: MutableList<Brick?>,
) {
    constructor(size: MapSize2d, borders: Borders):
            this(size, borders, MutableList<Brick?>(size.tiles()) { null })

    open fun size() = size
    fun borders() = borders

    fun addSingleBlock(x: Int, y: Int, grammar: ShapeGrammar) {
        val mapIndex = size.toIndexRisky(x, y)

        map[mapIndex] = Brick(grammar, BLOCK_SIZE)
    }

    fun addHorizontalBrick(x: Int, y: Int, grammar: ShapeGrammar, length: Int) {

        /*if (y < 0 || y >= size().height) {
            return
        }*/

        var limitedX = x
        var limitedLength = length

        if (x < 0) {
            val remainingLength = length + x

            if (borders.left && remainingLength > 0) {
                limitedX = 0
                limitedLength = remainingLength
            } else {
                return
            }
        } else if (borders.right && x + length > size().width) {
            val maxLength = size().width - x

            limitedLength = length.coerceAtMost(maxLength)
        }

        addBrick(limitedX, y, Brick(grammar,  MapSize2d(limitedLength, 1)))
    }

    fun addVerticalBrick(x: Int, y: Int, grammar: ShapeGrammar, length: Int) {
        /*if (x < 0 || x >= size().width) {
            return
        }*/

        var limitedY = y
        var limitedLength = length

        if (y < 0) {
            val remainingLength = length + y

            if (borders.top && remainingLength > 0) {
                limitedY = 0
                limitedLength = remainingLength
            } else {
                return
            }
        } else if (borders.bottom && y + length > size().height) {
            val maxLength = size().height - y

            limitedLength = length.coerceAtMost(maxLength)
        }

        addBrick(x, limitedY, Brick(grammar,  MapSize2d(1, limitedLength)))
    }

    protected open fun addBrick(x: Int, y: Int, brick: Brick) {
        val mapIndex = size.toIndexRisky(x, y)

        map[mapIndex] = brick
    }

    fun createSubSections(
        subSize: MapSize2d,
        addSubSection: (SubSectionBuilder, MapPoint2d) -> Unit,
    ) {
        val offset = borders.calculateTileOffset2(size(), subSize)
        val subSections = MapSize2d(
            ceil((size().width - offset.x) / subSize.width.toDouble()).toInt(),
            ceil((size().height - offset.y) / subSize.height.toDouble()).toInt(),
        )

        repeat(subSections.height) { subSectionY ->
            val subBorders = Borders(left=borders.left, tileX = -1, tileY = subSectionY)
            val subIndex = MapPoint2d(-1, subSectionY)
            val subStart = subIndex * subSize
            val limitedSubSize = limitSubSize(subStart, subSize)
            val subSection = SubSectionBuilder(
                size,
                subBorders,
                map,
                subIndex,
                limitedSubSize,
                MapPoint2d(0, subSize.height * subSectionY),
            )

            addSubSection(
                subSection,
                MapPoint2d(
                    offset.x - subSize.width,
                    offset.y,
                ),
            )

            repeat(subSections.width) { subSectionX ->
                val subIndex = MapPoint2d(subSectionX, subSectionY)
                val subStart = subIndex * subSize
                val limitedSubSize = limitSubSize(subStart, subSize)
                val subBorders = calculateSubBorders(borders, subSections, subSectionX, subSectionY)
                val subSection = SubSectionBuilder(
                    size,
                    subBorders,
                    map,
                    subIndex,
                    limitedSubSize,
                    subStart + offset,
                )

                logger.info { "subSection=${subIndex} subBorders=$subBorders offset=$offset subStart=$subStart subSize=$limitedSubSize" }

                addSubSection(subSection, MapPoint2d())
            }
        }
    }

    private fun limitSubSize(subStart: MapPoint2d, subSize: MapSize2d): MapSize2d {
        val subEnd = subStart + subSize;

        return MapSize2d(
            limitSubSize(subEnd.x, subSize.width, size.width),
            limitSubSize(subEnd.y, subSize.height, size.height),
        )
    }

    private fun limitSubSize(subEnd: Int, subSize: Int, size: Int) =if (subEnd > size) {
        subSize - (subEnd - size)
    } else {
        subSize
    }

    fun finish() = TileMap2d(size, map)
}

class SubSectionBuilder(
    size: MapSize2d,
    borders: Borders,
    map: MutableList<Brick?>,
    val subIndex: MapPoint2d,
    private val subSize: MapSize2d,
    private val offset: MapPoint2d,
): BrickMapBuilder(size, borders, map) {

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