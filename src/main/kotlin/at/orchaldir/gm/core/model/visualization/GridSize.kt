package at.orchaldir.gm.core.model.visualization

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

}

@Serializable
@SerialName("Square")
data class SquareGrid(
    val size: Int,
) : GridSize()

@Serializable
@SerialName("RowsAndColumns")
data class RowsAndColumns(
    val rows: Int,
    val columns: Int,
) : GridSize()