package at.orchaldir.gm.core.model.item.common

import at.orchaldir.gm.core.model.item.common.StitchType.Kettle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_STITCHES = 2
const val MAX_STITCHES = 20

enum class SewingPatternType {
    Repeated,
    Simple,
    Complex,
}

@Serializable
sealed class SewingPattern : MadeFromParts {

    fun getType() = when (this) {
        is RepeatedStitch -> SewingPatternType.Repeated
        is SimpleSewingPattern -> SewingPatternType.Simple
        is ComplexSewingPattern -> SewingPatternType.Complex
    }
}

@Serializable
@SerialName("Repeated")
data class RepeatedStitch(
    val cord: ItemPart = MadeFromCord(),
    val thickness: Size = Size.Medium,
    val width: Size = Size.Medium,
    val stitch: StitchType = Kettle,
    val count: Int = 2,
) : SewingPattern() {

    override fun parts() = listOf(cord)

}

@Serializable
@SerialName("Simple")
data class SimpleSewingPattern(
    val cord: ItemPart = MadeFromCord(),
    val thickness: Size = Size.Medium,
    val width: Size = Size.Medium,
    val stitches: List<StitchType> = listOf(Kettle, Kettle, Kettle, Kettle),
) : SewingPattern() {

    override fun parts() = listOf(cord)

}

@Serializable
data class ComplexStitch(
    val cord: ItemPart = MadeFromCord(),
    val thickness: Size = Size.Medium,
    val width: Size = Size.Medium,
    val stitch: StitchType = Kettle,
)

@Serializable
@SerialName("Complex")
data class ComplexSewingPattern(
    val stitches: List<ComplexStitch>,
) : SewingPattern() {

    override fun parts() = stitches.map { it.cord }

}