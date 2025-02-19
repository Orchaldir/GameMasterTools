package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.renderer.model.NamedColor
import kotlinx.serialization.Serializable

@Serializable
enum class Color {
    Aqua,
    Black,
    Blue,
    Chocolate,
    Crimson,
    DimGray,
    Fuchsia,
    Gold,
    Gray,
    Green,
    Indigo,
    Lavender,
    LightGray,
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
    SteelBlue,
    Teal,
    White,
    Yellow;

    fun toRender() = NamedColor(name)
}