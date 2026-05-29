package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class GridSizeType {
    Square,
    RowsAndColumns,
}

@Serializable
sealed class GridSize {

    fun getType() = when (this) {
        is SquareGrid -> GridSizeType.Square
        is RowsAndColumns -> GridSizeType.RowsAndColumns
    }

    fun process(aabb: AABB, function: (Point2d, MapSize2d, Size2d) -> Unit) = when (this) {
        is SquareGrid -> processGrid(aabb, MapSize2d.square(size), function)
        is RowsAndColumns -> processGrid(aabb, size, function)
    }

    fun processGrid(
        aabb: AABB,
        size: MapSize2d,
        function: (Point2d, MapSize2d, Size2d) -> Unit,
    ) = function(
        aabb.start,
        size,
        aabb.size / size,
    )

}

@Serializable
@SerialName("Square")
data class SquareGrid(
    val size: Int,
) : GridSize()

@Serializable
@SerialName("RowsAndColumns")
data class RowsAndColumns(
    val size: MapSize2d,
) : GridSize()