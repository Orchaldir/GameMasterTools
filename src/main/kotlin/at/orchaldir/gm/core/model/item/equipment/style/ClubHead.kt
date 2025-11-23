package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.RotatedShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ClubHeadType {
    None,
    Simple,
    SimpleFlanged,
    ComplexFlanged,
    MorningStar,
}

@Serializable
sealed interface ClubHead : MadeFromParts {

    fun getType() = when (this) {
        is NoClubHead -> ClubHeadType.None
        is SimpleClubHead -> ClubHeadType.Simple
        is SimpleFlangedHead -> ClubHeadType.SimpleFlanged
        is ComplexFlangedHead -> ClubHeadType.ComplexFlanged
        is MorningStar -> ClubHeadType.MorningStar
    }

    override fun parts() = when (this) {
        is NoClubHead -> emptyList()
        is SimpleClubHead -> listOf(part)
        is SimpleFlangedHead -> listOf(part)
        is ComplexFlangedHead -> listOf(part)
        is MorningStar -> listOf(part)
    }

    override fun mainMaterial() = when (this) {
        is NoClubHead -> null
        is SimpleClubHead -> part.material
        is SimpleFlangedHead -> part.material
        is ComplexFlangedHead -> part.material
        is MorningStar -> part.material
    }
}

@Serializable
@SerialName("None")
data object NoClubHead : ClubHead

@Serializable
@SerialName("Simple")
data class SimpleClubHead(
    val shape: ComplexShape = UsingCircularShape(),
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("SimpleFlanged")
data class SimpleFlangedHead(
    val shape: ComplexShape = UsingCircularShape(),
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("ComplexFlanged")
data class ComplexFlangedHead(
    val shape: RotatedShape,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("MorningStar")
data class MorningStar(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ClubHead