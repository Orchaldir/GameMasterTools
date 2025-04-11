package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
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

    override fun parts() = when (this) {
        is DangleEarring -> wire.parts() + top.parts() + bottom.parts()
        is DropEarring -> wire.parts() + top.parts() + bottom.parts()
        is HoopEarring -> wire.parts()
        is StudEarring -> ornament.parts()
    }
}

@Serializable
@SerialName("Dangle")
data class DangleEarring(
    val top: Ornament = SimpleOrnament(),
    val bottom: Ornament = SimpleOrnament(),
    val sizes: List<Size> = listOf(Size.Medium, Size.Large),
    val wire: ColorItemPart = ColorItemPart(Color.Gold),
) : EarringStyle()

@Serializable
@SerialName("Drop")
data class DropEarring(
    val topSize: Factor,
    val bottomSize: Factor,
    val wireLength: Factor,
    val top: Ornament = SimpleOrnament(),
    val bottom: Ornament = SimpleOrnament(),
    val wire: ColorItemPart = ColorItemPart(Color.Gold),
) : EarringStyle()

@Serializable
@SerialName("Hoop")
data class HoopEarring(
    val length: Factor,
    val thickness: Size = Size.Medium,
    val wire: ColorItemPart = ColorItemPart(Color.Gold),
) : EarringStyle()

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle()
