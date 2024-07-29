package at.orchaldir.gm.core.model.item.style

import at.orchaldir.gm.core.model.appearance.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OpeningStyle

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
) : OpeningStyle()

@Serializable
@SerialName("Zipper")
data class Zipper(
    val color: Color = Color.Silver,
) : OpeningStyle()