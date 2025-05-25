package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_PROTECTED_CORNER_SIZE = fromPercentage(20)
val DEFAULT_PROTECTED_EDGE_WIDTH = fromPercentage(10)

enum class EdgeProtectionType {
    None,
    Corners,
    Edge,
}

@Serializable
sealed class EdgeProtection : MadeFromParts {

    fun getType() = when (this) {
        is NoEdgeProtection -> EdgeProtectionType.None
        is ProtectedCorners -> EdgeProtectionType.Corners
        is ProtectedEdge -> EdgeProtectionType.Edge
    }
}

@Serializable
@SerialName("None")
data object NoEdgeProtection : EdgeProtection()

@Serializable
@SerialName("Corners")
data class ProtectedCorners(
    val shape: CornerShape = CornerShape.Triangle,
    val size: Factor = DEFAULT_PROTECTED_CORNER_SIZE,
    val main: ColorItemPart = ColorItemPart(),
) : EdgeProtection() {

    override fun parts() = listOf(main)

}

@Serializable
@SerialName("Edge")
data class ProtectedEdge(
    val width: Factor = DEFAULT_PROTECTED_EDGE_WIDTH,
    val main: ColorItemPart = ColorItemPart(),
) : EdgeProtection() {

    override fun parts() = listOf(main)

}

