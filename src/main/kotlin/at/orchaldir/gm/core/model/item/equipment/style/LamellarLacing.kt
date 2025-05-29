package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_THICKNESS = fromPercentage(1)
val DEFAULT_THICKNESS = fromPercentage(10)
val MAX_THICKNESS = fromPercentage(20)

val MIN_STRIPE_WIDTH = fromPercentage(1)
val DEFAULT_STRIPE_WIDTH = fromPercentage(10)
val MAX_STRIPE_WIDTH = fromPercentage(20)

enum class LamellarLacingType {
    None,
    Diagonal,
    FourSides,
    Stripe,
}

@Serializable
sealed class LamellarLacing : MadeFromParts {

    fun getType() = when (this) {
        NoLacing -> LamellarLacingType.None
        is DiagonalLacing -> LamellarLacingType.Diagonal
        is FourSidesLacing -> LamellarLacingType.FourSides
        is LacingAndStripe -> LamellarLacingType.Stripe
    }

    override fun parts() = when (this) {
        NoLacing -> emptyList()
        is DiagonalLacing -> listOf(lacing)
        is FourSidesLacing -> listOf(lacing)
        is LacingAndStripe -> listOf(lacing, stripe)
    }
}

@Serializable
@SerialName("None")
data object NoLacing : LamellarLacing()

@Serializable
@SerialName("Diagonal")
data class DiagonalLacing(
    val lacing: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
    val thickness: Factor = DEFAULT_THICKNESS,
) : LamellarLacing()

@Serializable
@SerialName("4")
data class FourSidesLacing(
    val lacing: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
) : LamellarLacing()

@Serializable
@SerialName("Stripe")
data class LacingAndStripe(
    val lacing: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
    val stripe: ColorSchemeItemPart = ColorSchemeItemPart(Color.SaddleBrown),
    val stripeWidth: Factor = DEFAULT_STRIPE_WIDTH,
) : LamellarLacing()
