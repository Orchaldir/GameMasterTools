package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornType {
    Curved,
}

@Serializable
sealed class Horn {

    fun getType() = when (this) {
        is CurvedHorn -> HornType.Curved
    }

    fun calculatePadding(headHeight: Distance) = when (this) {
        is CurvedHorn -> headHeight * length
    }

}

@Serializable
@SerialName("Curved")
data class CurvedHorn(
    val length: Factor,
    val width: Factor,
    val position: HornPosition,
    val orientationOffset: Orientation,
    val curve: HornCurve,
    val color: Color,
) : Horn()


