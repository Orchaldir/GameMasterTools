package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.Serializable

val MIN_SPEAR_LENGTH = fromPercentage(5)
val DEFAULT_SPEAR_LENGTH = fromPercentage(10)
val MAX_SPEAR_LENGTH = fromPercentage(20)

val MIN_SPEAR_WIDTH = fromPercentage(10)
val DEFAULT_SPEAR_WIDTH = fromPercentage(50)
val MAX_SPEAR_WIDTH = fromPercentage(75)

@Serializable
data class SpearHead(
    val shape: SpearShape = SpearShape.Leaf,
    val length: Factor = DEFAULT_SPEAR_LENGTH,
    val width: Factor = DEFAULT_SPEAR_WIDTH,
    val head: ColorSchemeItemPart = ColorSchemeItemPart(),
) : MadeFromParts {

    override fun parts() = listOf(head)

}
