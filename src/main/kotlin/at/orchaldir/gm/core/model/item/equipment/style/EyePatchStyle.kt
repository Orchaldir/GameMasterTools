package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyePatchStyleType {
    Simple,
}

@Serializable
sealed class EyePatchStyle {

    fun getType() = when (this) {
        is SimpleEyePatchStyle -> EyePatchStyleType.Simple
    }

    fun contains(id: MaterialId) = when (this) {
        is SimpleEyePatchStyle -> material == id
    }

    fun getMaterials() = when (this) {
        is SimpleEyePatchStyle -> setOf(material)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleEyePatchStyle(
    val shape: EyePatchShape = EyePatchShape.Ellipse,
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchStyle()

