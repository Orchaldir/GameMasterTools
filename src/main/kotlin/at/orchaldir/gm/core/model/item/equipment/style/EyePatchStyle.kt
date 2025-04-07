package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val VALID_LENSES = LensShape.entries
    .filter { it != LensShape.WarpAround }

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
    val shape: LensShape = LensShape.Rectangle,
    val color: Color = Color.Black,
    val material: MaterialId = MaterialId(0),
) : EyePatchStyle() {

    init {
        require(VALID_LENSES.contains(shape)) { "SimpleEyePatch has an invalid shape $shape!" }
    }
}

@Serializable
@SerialName("Ornament")
data class OrnamentAsEyePatch(
    val ornament: Ornament,
) : EyePatchStyle()

