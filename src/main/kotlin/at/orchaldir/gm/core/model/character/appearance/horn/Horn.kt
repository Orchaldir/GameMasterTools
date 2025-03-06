package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Factor
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

}

@Serializable
@SerialName("Curved")
data class CurvedHorn(
    val distance: Factor,
    val width: Factor,
    val position: HornPosition,
    val curve: HornCurve,
) : Horn()


