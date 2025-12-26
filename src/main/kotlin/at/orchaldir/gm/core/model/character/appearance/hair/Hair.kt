package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairType {
    None,
    Exotic,
}

@Serializable
sealed class Hair {

    fun getType() = when (this) {
        NoHair -> HairType.None
        is ExoticHair -> HairType.Exotic
    }

}

@Serializable
@SerialName("None")
data object NoHair : Hair()

@Serializable
@SerialName("Exotic")
data class ExoticHair(
    val cut: HairCut,
    val color: Color,
) : Hair()

