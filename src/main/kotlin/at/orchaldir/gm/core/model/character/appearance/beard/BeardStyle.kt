package at.orchaldir.gm.core.model.character.appearance.beard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BeardStyle

@Serializable
@SerialName("Moustache")
data class Moustache(
    val moustacheStyle: MoustacheStyle,
) : BeardStyle()

@Serializable
@SerialName("Goatee")
data class Goatee(
    val goateeStyle: GoateeStyle,
) : BeardStyle()

@Serializable
@SerialName("GoateeAndMoustache")
data class GoateeAndMoustache(
    val moustacheStyle: MoustacheStyle,
    val goateeStyle: GoateeStyle,
) : BeardStyle()
