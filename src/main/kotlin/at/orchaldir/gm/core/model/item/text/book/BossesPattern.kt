package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BossesPatternType {
    None,
    Simple,
}

@Serializable
sealed class BossesPattern : MadeFromParts {

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
    val boss: ColorItemPart = ColorItemPart(),
) : BossesPattern() {

    override fun parts() = listOf(boss)

}

