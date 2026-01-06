package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_GRIP_ROWS = 3
const val DEFAULT_GRIP_ROWS = 5
const val MAX_GRIP_ROWS = 20

enum class GripType {
    Simple,
    Bound,
}

@Serializable
sealed interface Grip : MadeFromParts {

    fun getType() = when (this) {
        is SimpleGrip -> GripType.Simple
        is BoundGrip -> GripType.Bound
    }

    override fun parts() = when (this) {
        is SimpleGrip -> listOf(part)
        is BoundGrip -> listOf(part)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleGrip(
    val shape: GripShape = GripShape.Straight,
    val part: FillLookupItemPart = FillLookupItemPart(),
) : Grip

@Serializable
@SerialName("Bound")
data class BoundGrip(
    val rows: Int = DEFAULT_GRIP_ROWS,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
) : Grip
