package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class NecklaceStyleType {
    Dangle,
    Drop,
    Pendant,
    Strand,
}

@Serializable
sealed class NecklaceStyle {

    fun getType() = when (this) {
        is DangleNecklace -> NecklaceStyleType.Dangle
        is DropNecklace -> NecklaceStyleType.Drop
        is PendantNecklace -> NecklaceStyleType.Pendant
        is StrandNecklace -> NecklaceStyleType.Strand
    }

    fun contains(id: MaterialId) = when (this) {
        is DangleNecklace -> dangle.contains(id) || line.contains(id)
        is DropNecklace -> drop.contains(id) || line.contains(id)
        is PendantNecklace -> ornament.contains(id) || line.contains(id)
        is StrandNecklace -> line.contains(id)
    }

    fun getMaterials() = when (this) {
        is DangleNecklace -> dangle.materials() + line.getMaterials()
        is DropNecklace -> drop.materials() + line.getMaterials()
        is PendantNecklace -> ornament.materials() + line.getMaterials()
        is StrandNecklace -> line.getMaterials()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleNecklace(
    val dangle: DangleEarring,
    val line: JewelryLine,
) : NecklaceStyle()

@Serializable
@SerialName("Drop")
data class DropNecklace(
    val drop: DropEarring,
    val line: JewelryLine,
) : NecklaceStyle()

@Serializable
@SerialName("Pendant")
data class PendantNecklace(
    val ornament: Ornament = SimpleOrnament(),
    val line: JewelryLine,
    val size: Size = Size.Medium,
) : NecklaceStyle()

@Serializable
@SerialName("Strand")
data class StrandNecklace(
    val strands: Int = 3,
    val line: JewelryLine,
    val padding: Size = Size.Medium,
) : NecklaceStyle() {

    init {
        require(strands > 0) { "The number of strands must be greater than 0!" }
    }
}

