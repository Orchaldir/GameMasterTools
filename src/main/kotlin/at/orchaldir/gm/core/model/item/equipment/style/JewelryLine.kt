package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class JewelryLineType {
    Chain,
    Ornament,
    Wire,
}

@Serializable
sealed class JewelryLine : MadeFromParts {

    fun getType() = when (this) {
        is Chain -> JewelryLineType.Chain
        is OrnamentLine -> JewelryLineType.Ornament
        is Wire -> JewelryLineType.Wire
    }

    fun getSizeOfSub() = when (this) {
        is Chain -> thickness
        is OrnamentLine -> size
        is Wire -> thickness
    }

    override fun parts() = when (this) {
        is Chain -> listOf(main)
        is OrnamentLine -> ornament.parts()
        is Wire -> listOf(main)
    }
}

@Serializable
@SerialName("Chain")
data class Chain(
    val thickness: Size = Size.Medium,
    val main: ColorItemPart = ColorItemPart(Color.Gold),
) : JewelryLine()

@Serializable
@SerialName("Ornament")
data class OrnamentLine(
    val ornament: Ornament,
    val size: Size = Size.Medium,
) : JewelryLine()

@Serializable
@SerialName("Wire")
data class Wire(
    val thickness: Size = Size.Medium,
    val main: ColorItemPart = ColorItemPart(Color.Gold),
) : JewelryLine() {

    constructor(thickness: Size, color: Color) : this(thickness, ColorItemPart(color))

}

