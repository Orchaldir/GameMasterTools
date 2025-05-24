package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.item.text.book.StitchType.Kettle
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_STITCHES = 2

enum class SewingPatternType {
    Simple,
    Complex,
}

@Serializable
sealed class SewingPattern : MadeFromParts {

    fun getType() = when (this) {
        is SimpleSewingPattern -> SewingPatternType.Simple
        is ComplexSewingPattern -> SewingPatternType.Complex
    }
}

@Serializable
@SerialName("Simple")
data class SimpleSewingPattern(
    val thread: ColorItemPart = ColorItemPart(),
    val size: Size = Size.Medium,
    val length: Size = Size.Medium,
    val stitches: List<StitchType> = listOf(Kettle, Kettle, Kettle, Kettle),
) : SewingPattern() {

    override fun parts() = listOf(thread)

}

@Serializable
data class ComplexStitch(
    val thread: ColorItemPart = ColorItemPart(),
    val size: Size = Size.Medium,
    val length: Size = Size.Medium,
    val stitch: StitchType = Kettle,
)

@Serializable
@SerialName("Complex")
data class ComplexSewingPattern(
    val stitches: List<ComplexStitch>,
) : SewingPattern() {

    override fun parts() = stitches.map { it.thread }

}