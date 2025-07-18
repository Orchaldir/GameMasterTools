package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HelmetFrontType {
    None,
    Nose,
    Eye,
    Face,
}

@Serializable
sealed class HelmetFront : MadeFromParts {

    fun getType() = when (this) {
        is NoHelmetFront -> HelmetFrontType.None
        is NoseProtection -> HelmetFrontType.Nose
        is EyeProtection -> HelmetFrontType.Eye
        is FaceProtection -> HelmetFrontType.Face
    }

    override fun parts() = when (this) {
        is NoHelmetFront -> emptyList()
        is NoseProtection -> listOf(part)
        is EyeProtection -> listOf(part)
        is FaceProtection -> listOf(part)
    }
}

@Serializable
@SerialName("None")
data object NoHelmetFront : HelmetFront()

@Serializable
@SerialName("Nose")
data class NoseProtection(
    val shape: NoseProtectionShape = NoseProtectionShape.Rectangle,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetFront()

@Serializable
@SerialName("Eye")
data class EyeProtection(
    val shape: EyeProtectionShape = EyeProtectionShape.RoundedRectangle,
    val hole: EyeHoleShape = EyeHoleShape.Almond,
    val nose: NoseProtectionShape? = null,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetFront()

@Serializable
@SerialName("Face")
data class FaceProtection(
    val shape: FaceProtectionShape = FaceProtectionShape.Heater,
    val eyeHole: EyeHoleShape = EyeHoleShape.Almond,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetFront()