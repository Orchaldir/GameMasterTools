package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class NecklaceStyleType {
    Dangle,
    Drop,
    Pendant,
    Strand,
}

@Serializable
sealed class NecklaceStyle : MadeFromParts {

    fun getType() = when (this) {
        is DangleNecklace -> NecklaceStyleType.Dangle
        is DropNecklace -> NecklaceStyleType.Drop
        is PendantNecklace -> NecklaceStyleType.Pendant
        is StrandNecklace -> NecklaceStyleType.Strand
    }

    override fun parts() = when (this) {
        is DangleNecklace -> line.parts() + dangle.parts()
        is DropNecklace -> line.parts() + drop.parts()
        is PendantNecklace -> line.parts() + ornament.parts()
        is StrandNecklace -> line.parts()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleNecklace(
    val dangle: DangleEarring,
    val line: LineStyle,
) : NecklaceStyle()

@Serializable
@SerialName("Drop")
data class DropNecklace(
    val drop: DropEarring,
    val line: LineStyle,
) : NecklaceStyle()

@Serializable
@SerialName("Pendant")
data class PendantNecklace(
    val ornament: Ornament = SimpleOrnament(),
    val line: LineStyle,
    val size: Size = Size.Medium,
) : NecklaceStyle()

@Serializable
@SerialName("Strand")
data class StrandNecklace(
    val strands: Int = 3,
    val line: LineStyle,
    val padding: Size = Size.Medium,
) : NecklaceStyle() {

    init {
        require(strands > 0) { "The number of strands must be greater than 0!" }
    }
}

