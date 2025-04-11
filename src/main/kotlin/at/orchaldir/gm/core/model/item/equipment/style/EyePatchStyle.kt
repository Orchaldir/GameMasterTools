package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
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
        is EyePatchWithEye -> listOf(cloth)
        is OrnamentAsEyePatch -> listOf(ornament)
        is SimpleEyePatch -> listOf(cloth)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleEyePatch(
    val shape: LensShape = LensShape.Rectangle,
    val cloth: FillItemPart = FillItemPart(Color.Black),
) : EyePatchStyle() {

    constructor(shape: LensShape, color: Color) : this(shape, FillItemPart(color))

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
    val cloth: FillItemPart = FillItemPart(Color.Black),
) : EyePatchStyle() {

    init {
        require(VALID_LENSES.contains(shape)) { "SimpleEyePatch has an invalid shape $shape!" }
    }
}

