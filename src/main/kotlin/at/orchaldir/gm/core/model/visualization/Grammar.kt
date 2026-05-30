package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.utils.math.Axis
import at.orchaldir.gm.utils.math.shape.RectangularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

var MIN_BRICK_LENGTH = 2
var DEFAULT_BRICK_LENGTH = 2
var MAX_BRICK_LENGTH = 5
var MIN_GRID_SIZE = 2
var MAX_GRID_SIZE = 1000

enum class GrammarType {
    RectangularShape,
    SingleBrick,
    DoNothing,
}

@Serializable
sealed class Grammar {

    fun getType() = when (this) {
        is RectangularShapeGrammar -> GrammarType.RectangularShape
        is SingleBrickGrammar -> GrammarType.SingleBrick
        DoNothingGrammar -> GrammarType.DoNothing
    }

}

@Serializable
@SerialName("RectangularShape")
data class RectangularShapeGrammar(
    val shape: RectangularShape,
    val part: ItemPart,
) : Grammar()

@Serializable
@SerialName("SingleBrick")
data class SingleBrickGrammar(
    val brick: Grammar,
    val size: GridSize,
    val pattern: SingleBrickPattern = SingleBrickPattern.Running,
    val length: Int = DEFAULT_BRICK_LENGTH,
) : Grammar()

@Serializable
@SerialName("DoNothing")
data object DoNothingGrammar : Grammar()
