package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d

data class Brick(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val x: Int,
    val y: Int,
    val size: MapSize2d,
)