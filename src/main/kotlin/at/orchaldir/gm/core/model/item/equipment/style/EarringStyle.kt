package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EarringStyleType {
    Dangle,
    Hoop,
    Stud,
}

@Serializable
sealed class EarringStyle {

    fun getType() = when (this) {
        is DangleEarring -> EarringStyleType.Dangle
        is HoopEarring -> EarringStyleType.Hoop
        is StudEarring -> EarringStyleType.Stud
    }

    fun contains(id: MaterialId) = when (this) {
        is DangleEarring -> ornament.contains(id)
        is HoopEarring -> material == id
        is StudEarring -> ornament.contains(id)
    }

    fun getMaterials() = when (this) {
        is DangleEarring -> ornament.getMaterials()
        is HoopEarring -> setOf(material)
        is StudEarring -> ornament.getMaterials()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleEarring(
    val ornament: Ornament = SimpleOrnament(),
    val sizes: List<Size> = listOf(Size.Medium, Size.Large),
    val wireColor: Color = Color.Gold,
    val wireMaterial: MaterialId = MaterialId(0),
) : EarringStyle()

@Serializable
@SerialName("Hoop")
data class HoopEarring(
    val length: Factor,
    val thickness: Size = Size.Medium,
    val color: Color = Color.Gold,
    val material: MaterialId = MaterialId(0),
) : EarringStyle()

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle()
