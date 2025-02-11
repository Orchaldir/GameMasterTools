package at.orchaldir.gm.core.model.character.appearance.beard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeardStyleType {
    Goatee,
    GoateeAndMoustache,
    Moustache,
    Shaved,
}

@Serializable
sealed class BeardStyle {

    fun getType() = when (this) {
        is Goatee -> BeardStyleType.Goatee
        is GoateeAndMoustache -> BeardStyleType.GoateeAndMoustache
        is Moustache -> BeardStyleType.Moustache
        ShavedBeard -> BeardStyleType.Shaved
    }

}

@Serializable
@SerialName("Shaved")
data object ShavedBeard : BeardStyle()

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
