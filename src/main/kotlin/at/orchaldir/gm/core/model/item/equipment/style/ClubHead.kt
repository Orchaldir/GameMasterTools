package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ClubHeadType {
    None,
    Simple,
    SimpleFlangedHead,
}

@Serializable
sealed interface ClubHead : MadeFromParts {

    fun getType() = when (this) {
        is NoClubHead -> ClubHeadType.None
        is SimpleClubHead -> ClubHeadType.Simple
        is SimpleFlangedHead -> ClubHeadType.SimpleFlangedHead
    }

    override fun parts() = when (this) {
        is NoClubHead -> emptyList()
        is SimpleClubHead -> listOf(part)
        is SimpleFlangedHead -> listOf(part)
    }

    override fun mainMaterial() = when (this) {
        is NoClubHead -> null
        is SimpleClubHead -> part.material
        is SimpleFlangedHead -> part.material
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
