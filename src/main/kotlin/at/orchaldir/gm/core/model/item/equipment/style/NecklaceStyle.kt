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
        is DangleNecklace -> dangle.contains(id) || strand.contains(id)
        is DropNecklace -> drop.contains(id) || strand.contains(id)
        is PendantNecklace -> ornament.contains(id) || strand.contains(id)
        is StrandNecklace -> strand.contains(id)
    }

    fun getMaterials() = when (this) {
        is DangleNecklace -> dangle.getMaterials() + strand.getMaterials()
        is DropNecklace -> drop.getMaterials() + strand.getMaterials()
        is PendantNecklace -> ornament.getMaterials() + strand.getMaterials()
        is StrandNecklace -> strand.getMaterials()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleNecklace(
    val dangle: DangleEarring,
    val strand: Strand,
) : NecklaceStyle()

@Serializable
@SerialName("Drop")
data class DropNecklace(
    val drop: DropEarring,
    val strand: Strand,
) : NecklaceStyle()

@Serializable
@SerialName("Pendant")
data class PendantNecklace(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
    val strand: Strand,
) : NecklaceStyle()

@Serializable
@SerialName("Strand")
data class StrandNecklace(
    val strands: Int = 3,
    val strand: Strand,
    val padding: Size = Size.Medium,
) : NecklaceStyle()

