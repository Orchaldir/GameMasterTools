package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.reducer.util.part.validateItemPart
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

var MIN_BRICK_LENGTH = 2
var DEFAULT_BRICK_LENGTH = 2
var MAX_BRICK_LENGTH = 5
var MIN_GRID_SIZE = 2
var MAX_GRID_SIZE = 1000
var MIN_SHRINK_FACTOR = ONE_PERCENT
var DEFAULT_SHRINK_FACTOR = TEN_PERCENTS
var MAX_SHRINK_FACTOR = THIRD

enum class ShapeGrammarType {
    DoNothing,
    BrickPattern,
    RectangularShape,
    Shrink,
}

@Serializable
sealed class ShapeGrammar {

    fun getType() = when (this) {
        is BrickPatternGrammar -> ShapeGrammarType.BrickPattern
        DoNothingShapeGrammar -> ShapeGrammarType.DoNothing
        is RectangularShapeGrammar -> ShapeGrammarType.RectangularShape
        is ShrinkGrammar -> ShapeGrammarType.Shrink
    }

    fun contains(material: MaterialId): Boolean = when (this) {
        is BrickPatternGrammar -> brick.contains(material)
        DoNothingShapeGrammar -> false
        is RectangularShapeGrammar -> part.contains(material)
        is ShrinkGrammar -> false
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is BrickPatternGrammar -> {
            size.validate(label, MIN_GRID_SIZE, MAX_GRID_SIZE)
            checkInt(length, "${label}'s brick length", MIN_BRICK_LENGTH, MAX_BRICK_LENGTH)
            brick.validate(state, "$label's brick")
        }

        DoNothingShapeGrammar -> doNothing()

        is RectangularShapeGrammar -> {
            validateItemPart(state, part, ItemPartType.entries)
        }

        is ShrinkGrammar -> validateFactor(
            factor,
            "${label}'s shrink factor",
            MIN_SHRINK_FACTOR,
            MAX_SHRINK_FACTOR,
        )
    }

}

@Serializable
@SerialName("BrickPattern")
data class BrickPatternGrammar(
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val size: GridSize = SquareGrid(10),
    val pattern: BrickPattern = BrickPattern.Running,
    val length: Int = DEFAULT_BRICK_LENGTH,
) : ShapeGrammar()

@Serializable
@SerialName("DoNothing")
data object DoNothingShapeGrammar : ShapeGrammar()

@Serializable
@SerialName("RectangularShape")
data class RectangularShapeGrammar(
    val part: ItemPart,
    val shape: RectangularShape = RectangularShape.Rectangle,
) : ShapeGrammar()

@Serializable
@SerialName("Shrink")
data class ShrinkGrammar(
    val grammar: ShapeGrammar,
    val factor: Factor = DEFAULT_SHRINK_FACTOR,
) : ShapeGrammar()
