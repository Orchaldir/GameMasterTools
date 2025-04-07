package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyePatchFixationType {
    None,
    OneBand,
    DiagonalBand,
    TwoBands,
}

@Serializable
sealed class EyePatchFixation {

    fun getType() = when (this) {
        NoFixation -> EyePatchFixationType.None
        is OneBand -> EyePatchFixationType.OneBand
        is DiagonalBand -> EyePatchFixationType.DiagonalBand
        is TwoBands -> EyePatchFixationType.TwoBands
    }

    fun contains(id: MaterialId) = when (this) {
        NoFixation -> false
        is OneBand -> material == id
        is DiagonalBand -> material == id
        is TwoBands -> material == id
    }

    fun getMaterials() = when (this) {
        NoFixation -> emptySet()
        is OneBand -> setOf(material)
        is DiagonalBand -> setOf(material)
        is TwoBands -> setOf(material)
    }
}

@Serializable
@SerialName("None")
data object NoFixation : EyePatchFixation()

@Serializable
@SerialName("OneBand")
data class OneBand(
    val size: Size = Size.Small,
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchFixation()

@Serializable
@SerialName("DiagonalBand")
data class DiagonalBand(
    val size: Size = Size.Small,
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchFixation()

@Serializable
@SerialName("TwoBands")
data class TwoBands(
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchFixation()


