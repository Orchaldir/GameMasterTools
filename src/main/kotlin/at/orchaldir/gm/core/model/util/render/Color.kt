package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.utils.renderer.model.NamedColor
import kotlinx.serialization.Serializable

@Serializable
enum class Color {
    AliceBlue,
    AntiqueWhite,
    Aqua,
    Black,
    Blue,
    BlueViolet,
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
    LightYellow,
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