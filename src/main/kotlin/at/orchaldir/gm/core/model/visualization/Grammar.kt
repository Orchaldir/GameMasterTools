package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.utils.math.Axis
import at.orchaldir.gm.utils.math.shape.RectangularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val pattern: SingleBrickPattern = SingleBrickPattern.RunningHalf,
    val axis: Axis = Axis.X,
) : Grammar()

@Serializable
@SerialName("DoNothing")
data object DoNothingGrammar : Grammar()
