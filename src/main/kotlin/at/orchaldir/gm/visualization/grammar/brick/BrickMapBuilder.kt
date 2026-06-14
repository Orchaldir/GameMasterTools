package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.visualization.grammar.Borders

private val BLOCK_SIZE = MapSize2d.square(1)

data class Brick(
    val grammar: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
) {
    override fun toString() = size.format()
}

data class BrickMapBuilder(
    private val size: MapSize2d,
    private val borders: Borders,
    private val map: MutableList<Brick?>,
) {
    constructor(size: MapSize2d, borders: Borders):
            this(size, borders, MutableList<Brick?>(size.tiles()) { null })

    fun size() = size
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
        } else if (borders.right && x + length > size.width) {
            val maxLength = size.width - x

            limitedLength = length.coerceAtMost(maxLength)
        }

        val mapIndex = size.toIndexRisky(limitedX, y)

        map[mapIndex] = Brick(grammar,  MapSize2d(limitedLength, 1))
    }

    fun finish() = TileMap2d(size, map)
}
