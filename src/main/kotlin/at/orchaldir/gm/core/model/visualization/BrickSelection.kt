package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BrickSelectionType {
    Uniform,
    HorizontalAndVertical,
}

@Serializable
sealed class BrickSelection {

    fun getType() = when (this) {
        is UniformBricks -> BrickSelectionType.Uniform
        is HorizontalAndVerticalBricks -> BrickSelectionType.HorizontalAndVertical
    }

    fun contains(material: MaterialId) = when (this) {
        is UniformBricks -> brick.contains(material)
        is HorizontalAndVerticalBricks -> horizontal.contains(material) ||  vertical.contains(material)
    }

    fun select(isHorizontal: Boolean) = when (this) {
        is UniformBricks -> brick
        is HorizontalAndVerticalBricks -> if (isHorizontal) {
            horizontal
        } else {
            vertical
        }
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is UniformBricks -> brick.validate(state, "$label's brick")
        is HorizontalAndVerticalBricks -> {
            horizontal.validate(state, "$label's horizontal brick")
            vertical.validate(state, "$label's vertical brick")
        }
    }
}

@Serializable
@SerialName("Uniform")
data class UniformBricks(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
) : BrickSelection()

@Serializable
@SerialName("HorizontalAndVertical")
data class HorizontalAndVerticalBricks(
    val horizontal: ShapeGrammar = DoNothingShapeGrammar,
    val vertical: ShapeGrammar = DoNothingShapeGrammar,
) : BrickSelection()
