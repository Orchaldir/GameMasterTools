package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BrickSelectionType {
    HorizontalAndVertical,
    Rows,
    Uniform,
}

@Serializable
sealed class BrickSelection {

    fun getType() = when (this) {
        is AlternateRows -> BrickSelectionType.Rows
        is HorizontalAndVerticalBricks -> BrickSelectionType.HorizontalAndVertical
        is UniformBricks -> BrickSelectionType.Uniform
    }

    fun contains(material: MaterialId) = when (this) {
        is AlternateRows -> rows.any { it.contains(material) }
        is HorizontalAndVerticalBricks -> horizontal.contains(material) || vertical.contains(material)
        is UniformBricks -> brick.contains(material)
    }

    fun select(row: Int, isHorizontal: Boolean) = when (this) {
        is AlternateRows -> rows[row.mod(rows.size)]
        is HorizontalAndVerticalBricks -> if (isHorizontal) {
            horizontal
        } else {
            vertical
        }

        is UniformBricks -> brick
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is AlternateRows -> {
            require(rows.size > 1) {
                "$label has too few rows!"
            }
            rows.withIndex().forEach { (row, brick) ->
                brick.validate(state, "$label's ${row + 1} row")
            }
        }

        is HorizontalAndVerticalBricks -> {
            horizontal.validate(state, "$label's horizontal brick")
            vertical.validate(state, "$label's vertical brick")
        }

        is UniformBricks -> brick.validate(state, "$label's brick")
    }
}

@Serializable
@SerialName("Rows")
data class AlternateRows(
    val rows: List<ShapeGrammar>,
) : BrickSelection()

@Serializable
@SerialName("HorizontalAndVertical")
data class HorizontalAndVerticalBricks(
    val horizontal: ShapeGrammar = DoNothingShapeGrammar,
    val vertical: ShapeGrammar = DoNothingShapeGrammar,
) : BrickSelection()

@Serializable
@SerialName("Uniform")
data class UniformBricks(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
) : BrickSelection()
