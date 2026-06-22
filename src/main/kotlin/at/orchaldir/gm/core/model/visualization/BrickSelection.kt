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

    fun contains(material: MaterialId): Boolean = when (this) {
        is UniformBricks -> brick.contains(material)
        is HorizontalAndVerticalBricks -> vertical.contains(material) || horizontal.contains(material)
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is UniformBricks -> brick.validate(state, "$label's brick")
        is HorizontalAndVerticalBricks -> {
            vertical.validate(state, "$label's vertical brick")
            horizontal.validate(state, "$label's horizontal brick")
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
