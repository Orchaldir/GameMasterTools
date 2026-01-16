package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LineStyleType {
    Chain,
    Ornament,
    Rope,
    Wire,
}

@Serializable
sealed class LineStyle : MadeFromParts {

    fun getType() = when (this) {
        is Chain -> LineStyleType.Chain
        is OrnamentLine -> LineStyleType.Ornament
        is Rope -> LineStyleType.Rope
        is Wire -> LineStyleType.Wire
    }

    fun getSizeOfSub() = when (this) {
        is Chain -> thickness
        is OrnamentLine -> size
        is Rope -> thickness
        is Wire -> thickness
    }

    override fun parts() = when (this) {
        is Chain -> listOf(main)
        is OrnamentLine -> ornament.parts()
        is Rope -> listOf(main)
        is Wire -> listOf(main)
    }
}

@Serializable
@SerialName("Chain")
data class Chain(
    val thickness: Size = Size.Medium,
    val main: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : LineStyle()

@Serializable
@SerialName("Ornament")
data class OrnamentLine(
    val ornament: Ornament,
    val size: Size = Size.Medium,
) : LineStyle()

@Serializable
@SerialName("Rope")
data class Rope(
    val main: ColorSchemeItemPart,
    val thickness: Size = Size.Medium,
) : LineStyle()

@Serializable
@SerialName("Wire")
data class Wire(
    val thickness: Size = Size.Medium,
    val main: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : LineStyle() {

    constructor(thickness: Size, color: Color) : this(thickness, ColorSchemeItemPart(color))

}

