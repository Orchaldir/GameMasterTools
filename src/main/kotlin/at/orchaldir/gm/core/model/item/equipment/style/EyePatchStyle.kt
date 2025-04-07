package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyePatchStyleType {
    Simple,
    Ornament,
}

@Serializable
sealed class EyePatchStyle {

    fun getType() = when (this) {
        is SimpleEyePatch -> EyePatchStyleType.Simple
        is OrnamentAsEyePatch -> EyePatchStyleType.Ornament
    }

    fun contains(id: MaterialId) = when (this) {
        is SimpleEyePatch -> material == id
        is OrnamentAsEyePatch -> ornament.contains(id)
    }

    fun getMaterials() = when (this) {
        is SimpleEyePatch -> setOf(material)
        is OrnamentAsEyePatch -> ornament.getMaterials()
    }
}

@Serializable
@SerialName("Simple")
data class SimpleEyePatch(
    val shape: EyePatchShape = EyePatchShape.Ellipse,
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchStyle()

@Serializable
@SerialName("Ornament")
data class OrnamentAsEyePatch(
    val ornament: Ornament,
) : EyePatchStyle()

