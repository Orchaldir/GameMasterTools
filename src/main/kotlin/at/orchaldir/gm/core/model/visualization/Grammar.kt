package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.reducer.util.part.validateItemPart
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.checkInt
import at.orchaldir.gm.utils.math.shape.RectangularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

var MIN_BRICK_LENGTH = 2
var DEFAULT_BRICK_LENGTH = 2
var MAX_BRICK_LENGTH = 5
var MIN_GRID_SIZE = 2
var MAX_GRID_SIZE = 1000

enum class GrammarType {
    BrickPattern,
    RectangularShape,
    DoNothing,
}

@Serializable
sealed class Grammar {

    fun getType() = when (this) {
        is BrickPatternGrammar -> GrammarType.BrickPattern
        is RectangularShapeGrammar -> GrammarType.RectangularShape
        DoNothingGrammar -> GrammarType.DoNothing
    }

    fun contains(material: MaterialId): Boolean = when (this) {
        is BrickPatternGrammar -> brick.contains(material)
        is RectangularShapeGrammar -> part.contains(material)
        DoNothingGrammar -> false
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is BrickPatternGrammar -> {
            size.validate(label, MIN_GRID_SIZE, MAX_GRID_SIZE)
            checkInt(length, "${label}'s brick length", MIN_BRICK_LENGTH, MAX_BRICK_LENGTH)
            brick.validate(state, "$label's brick")
        }
        is RectangularShapeGrammar -> {
            validateItemPart(state, part, ItemPartType.entries)
        }
        DoNothingGrammar -> doNothing()
    }

}

@Serializable
@SerialName("BrickPattern")
data class BrickPatternGrammar(
    val brick: Grammar,
    val size: GridSize,
    val pattern: SingleBrickPattern = SingleBrickPattern.Running,
    val length: Int = DEFAULT_BRICK_LENGTH,
) : Grammar()

@Serializable
@SerialName("RectangularShape")
data class RectangularShapeGrammar(
    val part: ItemPart,
    val shape: RectangularShape = RectangularShape.Rectangle,
) : Grammar()

@Serializable
@SerialName("DoNothing")
data object DoNothingGrammar : Grammar()
