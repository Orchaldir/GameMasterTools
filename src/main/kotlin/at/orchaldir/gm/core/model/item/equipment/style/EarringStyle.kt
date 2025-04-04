package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EarringStyleType {
    Dangle,
    Stud,
}

@Serializable
sealed class EarringStyle {

    fun getType() = when (this) {
        is DangleEarring -> EarringStyleType.Dangle
        is StudEarring -> EarringStyleType.Stud
    }

    fun contains(id: MaterialId) = when (this) {
        is DangleEarring -> ornament.contains(id)
        is StudEarring -> ornament.contains(id)
    }

    fun getMaterials() = when (this) {
        is DangleEarring -> ornament.getMaterials()
        is StudEarring -> ornament.getMaterials()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleEarring(
    val ornament: Ornament = SimpleOrnament(),
    val sizes: List<Size> = listOf(Size.Medium, Size.Large),
) : EarringStyle()

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle()
