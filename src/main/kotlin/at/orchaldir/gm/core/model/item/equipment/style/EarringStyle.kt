package at.orchaldir.gm.core.model.item.equipment.style

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
}

@Serializable
@SerialName("Stud")
data class StudEarring(
    val shape: StudShape = StudShape.Circle,
    val size: Size = Size.Medium,
) : EarringStyle()
