package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.utils.renderer.NamedColor
import kotlinx.serialization.Serializable

@Serializable
enum class Color {
    Aqua,
    Black,
    Blue,
    Fuchsia,
    Gray,
    Green,
    Lime,
    Maroon,
    Navy,
    Olive,
    Orange,
    Purple,
    Red,
    SaddleBrown,
    Silver,
    Teal,
    White,
    Yellow;

    fun toRender() = NamedColor(name)
}