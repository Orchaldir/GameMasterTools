package at.orchaldir.gm.visualization.grammar.brick

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
        var limitedX = x
        var limitedLength = length

        if (x < 0) {
            val remainingLength = length + x

            if (borders.left) {
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

    protected open fun addBrick(x: Int, y: Int, brick: Brick) {
        val mapIndex = size.toIndexRisky(x, y)

        map[mapIndex] = brick
    }

    fun createSubSections(
        subSize: MapSize2d,
        addSubSection: (SubSectionBuilder) -> Unit,
    ) {
        val offset = borders.calculateTileOffset2(size(), subSize)
        val limitedGridSize = subSize - offset
        val subSections = MapSize2d(
            ceil((size().width - offset.x) / subSize.width.toDouble()).toInt(),
            ceil((size().height - offset.y) / subSize.height.toDouble()).toInt(),
        )

        repeat(subSections.height) { subSectionY ->
            repeat(subSections.width) { subSectionX ->
                val subIndex = MapPoint2d(subSectionX, subSectionY)
                val subStart = subIndex * subSize + offset
                val subBorders = calculateSubBorders(borders, subSections, subSectionX, subSectionY)
                val subSection = SubSectionBuilder(
                    size,
                    subBorders,
                    map,
                    subIndex,
                    limitedGridSize,
                    subStart,
                )

                addSubSection(subSection)
            }
        }
    }

    fun finish() = TileMap2d(size, map)
}

class SubSectionBuilder(
    size: MapSize2d,
    borders: Borders,
    map: MutableList<Brick?>,
    private val subSection: MapPoint2d,
    private val subSize: MapSize2d,
    private val subStart: MapPoint2d,
): BrickMapBuilder(size, borders, map) {

    override fun size() = subSize

    override fun addBrick(x: Int, y: Int, brick: Brick) {
        val mapIndex = size.toIndexRisky(subStart.x + x, subStart.y + y)

        map[mapIndex] = brick
    }
}
