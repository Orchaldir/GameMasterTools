package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EarringStyleType {
    Dangle,
    Drop,
    Hoop,
    Stud,
}

@Serializable
sealed class EarringStyle : MadeFromParts {

    fun getType() = when (this) {
        is DangleEarring -> EarringStyleType.Dangle
        is DropEarring -> EarringStyleType.Drop
        is HoopEarring -> EarringStyleType.Hoop
        is StudEarring -> EarringStyleType.Stud
    }
}

@Serializable
@SerialName("Dangle")
data class DangleEarring(
    val top: Ornament = SimpleOrnament(),
    val bottom: Ornament = SimpleOrnament(),
    val sizes: List<Size> = listOf(Size.Medium, Size.Large),
    val wire: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : EarringStyle() {

    override fun parts() = top.parts() + bottom.parts() + wire

}

@Serializable
@SerialName("Drop")
data class DropEarring(
    val topSize: Factor,
    val bottomSize: Factor,
    val wireLength: Factor,
    val top: Ornament = SimpleOrnament(),
    val bottom: Ornament = SimpleOrnament(),
    val wire: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : EarringStyle() {

    override fun parts() = top.parts() + bottom.parts() + wire

}

@Serializable
@SerialName("Hoop")
data class HoopEarring(
    val length: Factor,
    val thickness: Size = Size.Medium,
    val wire: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : EarringStyle() {

    override fun parts() = listOf(wire)

}

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle() {

    override fun parts() = ornament.parts()

}
