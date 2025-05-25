package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OpeningType {
    NoOpening,
    SingleBreasted,
    DoubleBreasted,
    Zipper,
}

@Serializable
sealed class OpeningStyle : MadeFromParts {

    fun getType() = when (this) {
        NoOpening -> OpeningType.NoOpening
        is SingleBreasted -> OpeningType.SingleBreasted
        is DoubleBreasted -> OpeningType.DoubleBreasted
        is Zipper -> OpeningType.Zipper
    }

    override fun parts() = when (this) {
        NoOpening -> emptyList()
        is SingleBreasted -> buttons.parts()
        is DoubleBreasted -> buttons.parts()
        is Zipper -> listOf(part)
    }
}

@Serializable
@SerialName("NoOpening")
data object NoOpening : OpeningStyle()

@Serializable
@SerialName("SingleBreasted")
data class SingleBreasted(
    val buttons: ButtonColumn = ButtonColumn(),
) : OpeningStyle()

@Serializable
@SerialName("DoubleBreasted")
data class DoubleBreasted(
    val buttons: ButtonColumn = ButtonColumn(),
    val spaceBetweenColumns: Size = Size.Medium,
) : OpeningStyle()

@Serializable
@SerialName("Zipper")
data class Zipper(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
) : OpeningStyle()