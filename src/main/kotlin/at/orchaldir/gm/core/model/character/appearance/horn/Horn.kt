package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornType {
    Complex,
}

@Serializable
sealed class Horn {

    fun getType() = when (this) {
        is ComplexHorn -> HornType.Complex
    }

}

@Serializable
@SerialName("Complex")
data class ComplexHorn(
    val distance: Factor,
    val width: Factor,
    val position: HornPosition = HornPosition.Top,
) : Horn()


