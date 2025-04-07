package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrnamentType {
    Simple,
    Border,
}

@Serializable
sealed class Ornament {

    fun getType() = when (this) {
        is SimpleOrnament -> OrnamentType.Simple
        is OrnamentWithBorder -> OrnamentType.Border
    }

    fun getShapeFromSub() = when (this) {
        is SimpleOrnament -> shape
        is OrnamentWithBorder -> shape
    }

    fun contains(id: MaterialId) = when (this) {
        is SimpleOrnament -> material == id
        is OrnamentWithBorder -> material == id || borderMaterial == id
    }

    fun getMaterials() = when (this) {
        is SimpleOrnament -> setOf(material)
        is OrnamentWithBorder -> setOf(material, borderMaterial)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleOrnament(
    val shape: OrnamentShape = OrnamentShape.Circle,
    val color: Color = Color.Gold,
    val material: MaterialId = MaterialId(0),
) : Ornament()

@Serializable
@SerialName("Border")
data class OrnamentWithBorder(
    val shape: OrnamentShape = OrnamentShape.Circle,
    val color: Color = Color.Red,
    val material: MaterialId = MaterialId(0),
    val borderColor: Color = Color.Gold,
    val borderMaterial: MaterialId = MaterialId(0),
) : Ornament()
