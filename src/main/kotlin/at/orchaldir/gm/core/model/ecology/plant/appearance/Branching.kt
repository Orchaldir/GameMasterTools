package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BranchingType {
    None,
    Simple,
}

@Serializable
sealed class Branching {

    fun getType() = when (this) {
        NoBranching -> BranchingType.None
        is SimpleBranching -> BranchingType.Simple
    }

}

@Serializable
@SerialName("None")
data object NoBranching : Branching()

@Serializable
@SerialName("Simple")
data class SimpleBranching(
    val maxCount: Int,
    val sidePattern: BranchSidePattern,
    val base: Factor,
    val maxLength: Factor,
    val length: BranchLength = BranchLength.Conical,
    val branch: Stem = Stem(),
    /**
     * The angle between the parent and the children.
     */
    val angle: Variance<Orientation> = Variance(QUARTER_CIRCLE),
) : Branching()
