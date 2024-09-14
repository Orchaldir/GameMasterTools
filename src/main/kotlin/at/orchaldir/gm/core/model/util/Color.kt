package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.renderer.NamedColor
import kotlinx.serialization.Serializable

@Serializable
enum class Color {
    Aqua,
    Black,
    Blue,
    Chocolate,
    Crimson,
    Fuchsia,
    Gold,
    Gray,
    Green,
    Indigo,
    Lavender,
    Lime,
    Maroon,
    Navy,
    Olive,
    Orange,
    OrangeRed,
    Pink,
    Purple,
    Red,
    SaddleBrown,
    Silver,
    SkyBlue,
    Teal,
    White,
    Yellow;

    fun toRender() = NamedColor(name)
}