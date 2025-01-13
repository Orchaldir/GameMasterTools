package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BossesPatternType {
    None,
    Simple,
}

@Serializable
sealed class BossesPattern {

    fun getType() = when (this) {
        is NoBosses -> BossesPatternType.None
        is SimpleBossesPattern -> BossesPatternType.Simple
    }
}

@Serializable
@SerialName("None")
data object NoBosses : BossesPattern()

@Serializable
@SerialName("Simple")
data class SimpleBossesPattern(
    val pattern: List<Int> = listOf(2, 1, 2),
    val shape: BossesShape = BossesShape.Circle,
    val size: Size = Size.Medium,
    val color: Color = Color.Gray,
    val material: MaterialId = MaterialId(0),
) : BossesPattern()

