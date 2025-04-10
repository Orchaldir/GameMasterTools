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
        is DangleNecklace -> dangle.contains(id)
        is DropNecklace -> drop.contains(id)
        is PendantNecklace -> ornament.contains(id)
        is StrandNecklace -> false
    }

    fun getMaterials() = when (this) {
        is DangleNecklace -> dangle.getMaterials()
        is DropNecklace -> drop.getMaterials()
        is PendantNecklace -> ornament.getMaterials()
        is StrandNecklace -> emptySet()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleNecklace(
    val dangle: DangleEarring,
) : NecklaceStyle()

@Serializable
@SerialName("Drop")
data class DropNecklace(
    val drop: DropEarring,
) : NecklaceStyle()

@Serializable
@SerialName("Pendant")
data class PendantNecklace(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : NecklaceStyle()

@Serializable
@SerialName("Strand")
data class StrandNecklace(
    val stands: Int = 3,
    val padding: Size = Size.Medium,
) : NecklaceStyle()

