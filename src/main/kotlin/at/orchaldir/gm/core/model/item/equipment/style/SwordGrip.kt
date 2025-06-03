package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DEFAULT_SWORD_GRIP_ROWS = 5

enum class SwordGripType {
    Simple,
    Bound,
}

@Serializable
sealed interface SwordGrip : MadeFromParts {

    fun getType() = when (this) {
        is SimpleSwordGrip -> SwordGripType.Simple
        is BoundSwordGrip -> SwordGripType.Bound
    }

    override fun parts() = when (this) {
        is SimpleSwordGrip -> listOf(part)
        is BoundSwordGrip -> listOf(part)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleSwordGrip(
    val shape: SwordGripShape = SwordGripShape.Straight,
    val part: FillLookupItemPart = FillLookupItemPart(),
) : SwordGrip

@Serializable
@SerialName("Bound")
data class BoundSwordGrip(
    val rows: Int = DEFAULT_SWORD_GRIP_ROWS,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
) : SwordGrip
