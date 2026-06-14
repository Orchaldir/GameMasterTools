package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d

data class Brick(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d,
) {
    override fun toString() = size.format()
}

data class BrickMapCreator(
    val size: MapSize2d,
    val map: MutableList<Brick?>,
) {
    constructor(size: MapSize2d): this(size, MutableList<Brick?>(size.tiles()) { null })


}
