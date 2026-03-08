package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
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
    val line: LineStyle = Wire(),
) : EarringStyle() {

    override fun parts() = top.parts() + bottom.parts() + line.parts()

}

@Serializable
@SerialName("Drop")
data class DropEarring(
    val topSize: Factor,
    val bottomSize: Factor,
    val lineLength: Factor,
    val top: Ornament = SimpleOrnament(),
    val bottom: Ornament = SimpleOrnament(),
    val line: LineStyle = Wire(),
) : EarringStyle() {

    override fun parts() = top.parts() + bottom.parts() + line.parts()

}

@Serializable
@SerialName("Hoop")
data class HoopEarring(
    val length: Factor,
    val wire: Wire = Wire(),
) : EarringStyle() {

    override fun parts() = wire.parts()

}

@Serializable
@SerialName("Stud")
data class StudEarring(
    val ornament: Ornament = SimpleOrnament(),
    val size: Size = Size.Medium,
) : EarringStyle() {

    override fun parts() = ornament.parts()

}
