package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
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
sealed class EdgeProtection {

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
    val color: Color = Color.Gray,
    val material: MaterialId = MaterialId(0),
) : EdgeProtection()

@Serializable
@SerialName("Edge")
data class ProtectedEdge(
    val width: Factor = DEFAULT_PROTECTED_EDGE_WIDTH,
    val color: Color = Color.Gray,
    val material: MaterialId = MaterialId(0),
) : EdgeProtection()

