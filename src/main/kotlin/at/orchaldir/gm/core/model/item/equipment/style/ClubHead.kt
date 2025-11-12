package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ClubHeadType {
    Simple,
}

@Serializable
sealed interface ClubHead : MadeFromParts {

    fun getType() = when (this) {
        is SimpleClubHead -> ClubHeadType.Simple
    }

    override fun parts() = when (this) {
        is SimpleClubHead -> listOf(part)
    }

    override fun mainMaterial() = when (this) {
        is SimpleClubHead -> part.material
    }
}

@Serializable
@SerialName("Simple")
data class SimpleClubHead(
    val shape: ComplexShape = UsingCircularShape(),
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ClubHead
