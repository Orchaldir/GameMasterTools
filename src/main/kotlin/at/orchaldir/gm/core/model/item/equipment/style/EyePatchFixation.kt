package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyePatchFixationType {
    None,
    OneBand,
    DiagonalBand,
    TwoBands,
}

@Serializable
sealed class EyePatchFixation : MadeFromParts {

    fun getType() = when (this) {
        NoFixation -> EyePatchFixationType.None
        is OneBand -> EyePatchFixationType.OneBand
        is DiagonalBand -> EyePatchFixationType.DiagonalBand
        is TwoBands -> EyePatchFixationType.TwoBands
    }

    override fun parts() = when (this) {
        is DiagonalBand -> listOf(band)
        NoFixation -> emptyList()
        is OneBand -> listOf(band)
        is TwoBands -> listOf(band)
    }
}

@Serializable
@SerialName("None")
data object NoFixation : EyePatchFixation()

@Serializable
@SerialName("OneBand")
data class OneBand(
    val size: Size = Size.Small,
    val band: ColorSchemeItemPart = ColorSchemeItemPart(Color.Black),
) : EyePatchFixation() {

    constructor(size: Size, color: Color) : this(size, ColorSchemeItemPart(color))

}

@Serializable
@SerialName("DiagonalBand")
data class DiagonalBand(
    val size: Size = Size.Small,
    val band: ColorSchemeItemPart = ColorSchemeItemPart(Color.Black),
) : EyePatchFixation() {

    constructor(size: Size, color: Color) : this(size, ColorSchemeItemPart(color))

}

@Serializable
@SerialName("TwoBands")
data class TwoBands(
    val band: ColorSchemeItemPart = ColorSchemeItemPart(Color.Black),
) : EyePatchFixation() {

    constructor(color: Color) : this(ColorSchemeItemPart(color))

}


