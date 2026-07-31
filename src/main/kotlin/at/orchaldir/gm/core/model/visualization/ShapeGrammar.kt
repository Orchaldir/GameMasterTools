package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.reducer.util.part.validateItemPart
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.RangeInt
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.TEN_PERCENTS
import at.orchaldir.gm.utils.math.THIRD
import at.orchaldir.gm.utils.math.checkInt
import at.orchaldir.gm.utils.math.validateFactor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

var MIN_BRICK_LENGTH = 2
var DEFAULT_BRICK_LENGTH = 2
var MAX_BRICK_LENGTH = 5
var BRICK_SIZE_RANGE = RangeInt(MIN_BRICK_LENGTH, MAX_BRICK_LENGTH)
var MIN_GRID_SIZE = 2
var MAX_GRID_SIZE = 1000
var MIN_SHRINK_FACTOR = ONE_PERCENT
var DEFAULT_SHRINK_FACTOR = TEN_PERCENTS
var MAX_SHRINK_FACTOR = THIRD

enum class ShapeGrammarType {
    DoNothing,
    Ashlar,
    BrickPattern,
    RectangularShape,
    Shrink,
}

@Serializable
sealed class ShapeGrammar {

    fun getType() = when (this) {
        is AshlarGrammar -> ShapeGrammarType.Ashlar
        is BrickPatternGrammar -> ShapeGrammarType.BrickPattern
        DoNothingShapeGrammar -> ShapeGrammarType.DoNothing
        is RectangularShapeGrammar -> ShapeGrammarType.RectangularShape
        is ShrinkGrammar -> ShapeGrammarType.Shrink
    }

    fun contains(material: MaterialId): Boolean = when (this) {
        is AshlarGrammar -> brick.contains(material)
        is BrickPatternGrammar -> bricks.contains(material)
        DoNothingShapeGrammar -> false
        is RectangularShapeGrammar -> part.contains(material)
        is ShrinkGrammar -> grammar.contains(material)
    }

    fun validate(state: State, label: String): Unit = when (this) {
        is AshlarGrammar -> {
            size.validate(label, MIN_GRID_SIZE, MAX_GRID_SIZE)
            brick.validate(state, label)
            BRICK_SIZE_RANGE.validateInt(brickWidth, "${label}'s brick width")
            BRICK_SIZE_RANGE.validateInt(brickHeight, "${label}'s brick height")
        }

        is BrickPatternGrammar -> {
            size.validate(label, MIN_GRID_SIZE, MAX_GRID_SIZE)
            checkInt(length, "${label}'s brick length", MIN_BRICK_LENGTH, MAX_BRICK_LENGTH)
            bricks.validate(state, label)
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
@SerialName("Ashlar")
data class AshlarGrammar(
    val size: GridSize = SquareGrid(10),
    val brick: ShapeGrammar = DoNothingShapeGrammar,
    val brickWidth: Int = MAX_BRICK_LENGTH,
    val brickHeight: Int = MAX_BRICK_LENGTH,
) : ShapeGrammar()

@Serializable
@SerialName("BrickPattern")
data class BrickPatternGrammar(
    val bricks: BrickSelection = UniformBricks(),
    val size: GridSize = SquareGrid(10),
    val pattern: BrickPattern = BrickPattern.Running,
    val length: Int = DEFAULT_BRICK_LENGTH,
) : ShapeGrammar() {

    constructor(
        brick: ShapeGrammar,
        size: GridSize = SquareGrid(10),
        pattern: BrickPattern = BrickPattern.Running,
        length: Int = DEFAULT_BRICK_LENGTH,
    ) : this(UniformBricks(brick), size, pattern, length)

}

@Serializable
@SerialName("DoNothing")
data object DoNothingShapeGrammar : ShapeGrammar()

@Serializable
@SerialName("RectangularShape")
data class RectangularShapeGrammar(
    val part: ItemPart,
    val shape: RectangularShape = RectangularShape.Rectangle,
) : ShapeGrammar() {

    constructor(color: Color) : this(MadeFromStone(color))

}

@Serializable
@SerialName("Shrink")
data class ShrinkGrammar(
    val grammar: ShapeGrammar,
    val factor: Factor = DEFAULT_SHRINK_FACTOR,
) : ShapeGrammar()
