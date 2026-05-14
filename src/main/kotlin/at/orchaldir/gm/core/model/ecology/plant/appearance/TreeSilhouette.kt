package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.THIRD
import at.orchaldir.gm.utils.math.validateFactor
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

    fun validate() = when (this) {
        NoTreeSilhouette -> doNothing()
        is SimpleTreeSilhouette -> {
            validateFactor(base, "silhouette's base", MIN_BRANCHING_BASE, MAX_BRANCHING_BASE)
            validateFactor(width, "silhouette's width", MIN_SILHOUETTE_WIDTH, MAX_SILHOUETTE_WIDTH)
        }
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
    val showStems: Boolean = false,
) : TreeSilhouette()
