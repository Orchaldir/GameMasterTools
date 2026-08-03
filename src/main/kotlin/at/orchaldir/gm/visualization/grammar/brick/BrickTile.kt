package at.orchaldir.gm.visualization.grammar.brick

import at.orchaldir.gm.core.model.visualization.DoNothingShapeGrammar
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import kotlinx.serialization.Serializable

enum class BrickTileType {
    Brick,
    Empty,
    Occupied,
}

@Serializable
sealed class BrickTile {

    fun getType() = when (this) {
        is Brick -> BrickTileType.Brick
        EmptyTile -> BrickTileType.Empty
        OccupiedTile -> BrickTileType.Occupied
    }

    fun isFree() = this is EmptyTile
    fun isFull() = this !is EmptyTile
}

data class Brick(
    val grammar: ShapeGrammar = DoNothingShapeGrammar,
    val size: MapSize2d = MapSize2d(1, 1),
) : BrickTile() {
    override fun toString() = size.format()
}

data object EmptyTile : BrickTile()

data object OccupiedTile : BrickTile()

