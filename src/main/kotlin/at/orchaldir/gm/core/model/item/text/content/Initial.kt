package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_INITIAL_SIZE: Factor = fromPercentage(150)
val DEFAULT_INITIAL_SIZE: Factor = fromPercentage(200)
val MAX_INITIAL_SIZE: Factor = fromPercentage(500)

enum class InitialType {
    Normal,
    Large,
    Simple,
}

@Serializable
sealed class Initial {

    fun getType() = when (this) {
        NormalInitial -> InitialType.Normal
        is LargeInitial -> InitialType.Large
        is FontInitial -> InitialType.Simple
    }

    fun contains(font: FontId) = when (this) {
        NormalInitial -> false
        is LargeInitial -> false
        is FontInitial -> fontOption.font() == font
    }
}

@Serializable
@SerialName("Normal")
data object NormalInitial : Initial()

@Serializable
@SerialName("Large")
data class LargeInitial(
    val size: Factor = DEFAULT_INITIAL_SIZE,
) : Initial() {

    init {
        require(size >= MIN_INITIAL_SIZE) { "Size is too small!" }
        require(size <= MAX_INITIAL_SIZE) { "Size is too large!" }
    }

}

@Serializable
@SerialName("Font")
data class FontInitial(
    val fontOption: FontOption = SolidFont(Distance.fromMillimeters(10)),
) : Initial()
