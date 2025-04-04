package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EarringStyleType {
    Stud,
}

@Serializable
sealed class EarringStyle {

    fun getType() = when (this) {
        is StudEarring -> EarringStyleType.Stud
    }

    fun contains(id: MaterialId) = when (this) {
        is StudEarring -> ornament.contains(id)
    }

    fun getMaterials() = when (this) {
        is StudEarring -> ornament.getMaterials()
    }
}

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle()
