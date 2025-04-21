package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.renderer.model.NamedColor
import kotlinx.serialization.Serializable

@Serializable
enum class Color {
    AntiqueWhite,
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
    Ivory,
    Lavender,
    LightGray,
    Lime,
    Maroon,
    Navy,
    Olive,
    Orange,
    OrangeRed,
    Peru,
    Pink,
    Purple,
    Red,
    SaddleBrown,
    Silver,
    SkyBlue,
    SteelBlue,
    Tan,
    Teal,
    Wheat,
    White,
    Yellow;

    fun toRender() = NamedColor(name)
}