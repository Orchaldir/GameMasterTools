package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_BRANCHES = 2
const val MAX_BRANCHES = 100
val MIN_BRANCH_CENTER = Orientation.fromDegrees(10)
val DEFAULT_BRANCH_CENTER = QUARTER_CIRCLE
val MAX_BRANCH_CENTER = Orientation.fromDegrees(170)
val MAX_BRANCH_OFFSET = Orientation.fromDegrees(20)
val MIN_BRANCH_OFFSET = -MAX_BRANCH_OFFSET

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
    val angle: Variance<Orientation> = Variance(DEFAULT_BRANCH_CENTER),
) : Branching()
