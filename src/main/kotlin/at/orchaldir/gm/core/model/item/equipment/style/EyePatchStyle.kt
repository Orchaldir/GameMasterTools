package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val VALID_LENSES = LensShape.entries
    .filter { it != LensShape.WarpAround }

enum class EyePatchStyleType {
    Simple,
    Ornament,
    Eye,
}

@Serializable
sealed class EyePatchStyle : MadeFromParts {

    fun getType() = when (this) {
        is EyePatchWithEye -> EyePatchStyleType.Eye
        is OrnamentAsEyePatch -> EyePatchStyleType.Ornament
        is SimpleEyePatch -> EyePatchStyleType.Simple
    }

    override fun parts() = when (this) {
        is EyePatchWithEye -> listOf(main)
        is OrnamentAsEyePatch -> ornament.parts()
        is SimpleEyePatch -> listOf(main)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleEyePatch(
    val shape: LensShape = LensShape.Rectangle,
    val main: FillLookupItemPart = FillLookupItemPart(Color.Black),
) : EyePatchStyle() {

    constructor(shape: LensShape, color: Color) : this(shape, FillLookupItemPart(color))

    init {
        require(VALID_LENSES.contains(shape)) { "SimpleEyePatch has an invalid shape $shape!" }
    }
}

@Serializable
@SerialName("Ornament")
data class OrnamentAsEyePatch(
    val ornament: Ornament,
) : EyePatchStyle()

@Serializable
@SerialName("Eye")
data class EyePatchWithEye(
    val eye: NormalEye,
    val shape: LensShape = LensShape.Rectangle,
    val main: FillLookupItemPart = FillLookupItemPart(Color.Black),
) : EyePatchStyle() {

    init {
        require(VALID_LENSES.contains(shape)) { "SimpleEyePatch has an invalid shape $shape!" }
    }
}

