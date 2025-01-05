package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.item.book.StitchType.Kettle
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_STITCHES = 2

enum class SewingPatternType {
    Simple,
    Complex,
}

@Serializable
sealed class SewingPattern {

    fun getType() = when (this) {
        is SimpleSewingPattern -> SewingPatternType.Simple
        is ComplexSewingPattern -> SewingPatternType.Complex
    }
}

@Serializable
@SerialName("Simple")
data class SimpleSewingPattern(
    val color: Color = Color.Red,
    val size: Size = Size.Medium,
    val length: Size = Size.Medium,
    val stitches: List<StitchType> = listOf(Kettle, Kettle, Kettle, Kettle),
) : SewingPattern()

@Serializable
data class ComplexStitch(
    val color: Color,
    val size: Size = Size.Medium,
    val length: Size = Size.Medium,
    val stitch: StitchType = Kettle,
)

@Serializable
@SerialName("Complex")
data class ComplexSewingPattern(
    val stitches: List<ComplexStitch>,
) : SewingPattern()