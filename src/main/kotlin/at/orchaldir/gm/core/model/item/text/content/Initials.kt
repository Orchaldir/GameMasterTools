package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_INITIAL_SIZE: Factor = fromPercentage(150)
val DEFAULT_INITIAL_SIZE: Factor = fromPercentage(200)
val MAX_INITIAL_SIZE: Factor = fromPercentage(500)

enum class InitialsType {
    Normal,
    Large,
    Font,
}

@Serializable
sealed class Initials {

    fun getType() = when (this) {
        NormalInitials -> InitialsType.Normal
        is LargeInitials -> InitialsType.Large
        is FontInitials -> InitialsType.Font
    }

    fun contains(font: FontId) = when (this) {
        NormalInitials -> false
        is LargeInitials -> false
        is FontInitials -> fontOption.font() == font
    }
}

@Serializable
@SerialName("Normal")
data object NormalInitials : Initials()

@Serializable
@SerialName("Large")
data class LargeInitials(
    val size: Factor = DEFAULT_INITIAL_SIZE,
    val position: InitialPosition = InitialPosition.DropCap,
) : Initials() {

    init {
        require(size >= MIN_INITIAL_SIZE) { "Size is too small!" }
        require(size <= MAX_INITIAL_SIZE) { "Size is too large!" }
    }

}

@Serializable
@SerialName("Font")
data class FontInitials(
    val fontOption: FontOption = SolidFont(Distance.fromMillimeters(10)),
    val position: InitialPosition = InitialPosition.DropCap,
) : Initials()
