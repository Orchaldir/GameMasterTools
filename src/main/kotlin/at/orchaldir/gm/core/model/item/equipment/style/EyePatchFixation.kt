package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromCord
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
        NoFixation -> emptyList()
        is OneBand -> band.parts()
        is DiagonalBand -> band.parts()
        is TwoBands -> band.parts()
    }
}

@Serializable
@SerialName("None")
data object NoFixation : EyePatchFixation()

@Serializable
@SerialName("OneBand")
data class OneBand(
    val band: LineStyle,
) : EyePatchFixation() {

    constructor(size: Size, color: Color = Color.Black) : this(Cord(MadeFromCord(color), size))

}

@Serializable
@SerialName("DiagonalBand")
data class DiagonalBand(
    val band: LineStyle,
) : EyePatchFixation() {

    constructor(size: Size, color: Color = Color.Black) : this(Cord(MadeFromCord(color), size))

}

@Serializable
@SerialName("TwoBands")
data class TwoBands(
    val band: LineStyle,
) : EyePatchFixation() {

    constructor(size: Size, color: Color = Color.Black) : this(Cord(MadeFromCord(color), size))

}


