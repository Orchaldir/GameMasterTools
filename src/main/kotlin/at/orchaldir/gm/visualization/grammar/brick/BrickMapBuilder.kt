package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d

private val BLOCK_SIZE = MapSize2d.square(1)

data class Brick(
    val grammar: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
) {
    override fun toString() = size.format()
}

data class BrickMapBuilder(
    val size: MapSize2d,
    val map: MutableList<Brick?>,
) {
    constructor(size: MapSize2d): this(size, MutableList<Brick?>(size.tiles()) { null })

    fun addSingleBlock(x: Int, y: Int, grammar: ShapeGrammar) {
        val mapIndex = size.toIndexRisky(x, y)

        map[mapIndex] = Brick(grammar, BLOCK_SIZE)
    }

    fun finish() = TileMap2d(size, map)
}
