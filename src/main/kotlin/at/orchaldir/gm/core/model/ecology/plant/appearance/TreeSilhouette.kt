package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_SILHOUETTE_WIDTH = Factor.fromPercentage(10)
val DEFAULT_SILHOUETTE_WIDTH = THIRD
val MAX_SILHOUETTE_WIDTH = Factor.fromPercentage(90)

enum class TreeSilhouetteType {
    None,
    Simple,
}

@Serializable
sealed class TreeSilhouette {

    fun getType() = when (this) {
        NoTreeSilhouette -> TreeSilhouetteType.None
        is SimpleTreeSilhouette -> TreeSilhouetteType.Simple
    }
}

@Serializable
@SerialName("None")
data object NoTreeSilhouette : TreeSilhouette()

@Serializable
@SerialName("Simple")
data class SimpleTreeSilhouette(
    val shape: TreeSilhouetteShape = TreeSilhouetteShape.Conical,
    val color: Color = Color.Green,
    val base: Factor = DEFAULT_BRANCHING_BASE,
    val width: Factor = DEFAULT_SILHOUETTE_WIDTH,
) : TreeSilhouette()
