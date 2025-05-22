package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeltHolesType {
    NoBeltHoles,
    OneRow,
    TwoRows,
    ThreeRows,
}

@Serializable
sealed class BeltHoles {

    fun getType() = when (this) {
        NoBeltHoles -> BeltHolesType.NoBeltHoles
        is OneRowOfBeltHoles -> BeltHolesType.OneRow
        is TwoRowsOfBeltHoles -> BeltHolesType.TwoRows
        is ThreeRowsOfBeltHoles -> BeltHolesType.ThreeRows
    }
}

@Serializable
@SerialName("NoBeltHoles")
data object NoBeltHoles : BeltHoles()

@Serializable
@SerialName("OneRow")
data class OneRowOfBeltHoles(
    val size: Size = Size.Small,
    val border: Color? = null,
) : BeltHoles()

@Serializable
@SerialName("TwoRows")
data class TwoRowsOfBeltHoles(
    val border: Color? = null,
) : BeltHoles()

@Serializable
@SerialName("ThreeRows")
data class ThreeRowsOfBeltHoles(
    val border: Color? = null,
) : BeltHoles()
