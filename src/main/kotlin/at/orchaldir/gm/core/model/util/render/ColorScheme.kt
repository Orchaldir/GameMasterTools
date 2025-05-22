package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val BATTLE_TYPE = "Color Scheme"

@JvmInline
@Serializable
value class ColorSchemeId(val value: Int) : Id<ColorSchemeId> {

    override fun next() = ColorSchemeId(value + 1)
    override fun type() = BATTLE_TYPE
    override fun value() = value

}

@Serializable
sealed interface ColorScheme : ElementWithSimpleName<ColorSchemeId>

@Serializable
@SerialName("1")
data class OneColor(
    val id: ColorSchemeId,
    val color: Color,
) : ColorScheme {

    override fun id() = id
    override fun name() = color.name

}

@Serializable
@SerialName("2")
data class TwoColors(
    val id: ColorSchemeId,
    val color0: Color,
    val color1: Color,
) : ColorScheme {

    override fun id() = id
    override fun name() = "$color0 & $color1"

}
