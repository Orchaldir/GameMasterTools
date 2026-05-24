package at.orchaldir.gm.core.model.visualization

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.utils.math.shape.RectangularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class GrammarType {
    RectangularShape,
    SimpleBrick,
    DoNothing,
}

@Serializable
sealed class Grammar {

    fun getType() = when (this) {
        is RectangularShapeGrammar -> GrammarType.RectangularShape
        is SimpleBrickGrammar -> GrammarType.SimpleBrick
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
@SerialName("SimpleBrick")
data class SimpleBrickGrammar(
    val brick: Grammar,
) : Grammar()

@Serializable
@SerialName("DoNothing")
data object DoNothingGrammar : Grammar()
