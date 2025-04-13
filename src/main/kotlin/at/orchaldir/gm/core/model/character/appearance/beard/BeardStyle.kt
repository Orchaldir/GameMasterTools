package at.orchaldir.gm.core.model.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeardStyleType {
    Braided,
    Goatee,
    GoateeAndMoustache,
    Moustache,
    Shaved,
}

@Serializable
sealed class BeardStyle {

    fun getType() = when (this) {
        is BraidedBeard -> BeardStyleType.Braided
        is Goatee -> BeardStyleType.Goatee
        is GoateeAndMoustache -> BeardStyleType.GoateeAndMoustache
        is Moustache -> BeardStyleType.Moustache
        ShavedBeard -> BeardStyleType.Shaved
    }

}

@Serializable
@SerialName("Braided")
data class BraidedBeard(
    val style: BraidedBeardStyle,
    val width: Size = Size.Medium,
    val length: HairLength = HairLength.MidBack,
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

@Serializable
@SerialName("Moustache")
data class Moustache(
    val moustacheStyle: MoustacheStyle,
) : BeardStyle()

@Serializable
@SerialName("Shaved")
data object ShavedBeard : BeardStyle()
