package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairType {
    None,
    Normal,
}

@Serializable
sealed class Hair {

    fun getType() = when (this) {
        NoHair -> HairType.None
        is NormalHair -> HairType.Normal
    }

}

@Serializable
@SerialName("None")
data object NoHair : Hair()

@Serializable
@SerialName("Normal")
data class NormalHair(
    val cut: HairCut,
    val color: HairColor,
) : Hair() {

    constructor(cut: HairCut, color: Color): this(cut, ExoticHairColor(color))

}

