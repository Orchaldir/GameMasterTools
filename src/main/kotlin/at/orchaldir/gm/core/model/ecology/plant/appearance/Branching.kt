package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_BRANCHES = 2
const val MAX_BRANCHES = 100

val MIN_BRANCHING_BASE = Factor.fromPercentage(10)
val DEFAULT_BRANCHING_BASE = THIRD
val MAX_BRANCHING_BASE = Factor.fromPercentage(90)

val MIN_BRANCH_LENGTH = Factor.fromPercentage(10)
val DEFAULT_BRANCH_LENGTH = QUARTER
val MAX_BRANCH_LENGTH = Factor.fromPercentage(200)

val MIN_BRANCH_CENTER = Orientation.fromDegrees(10)
val DEFAULT_BRANCH_CENTER = QUARTER_CIRCLE
val MAX_BRANCH_CENTER = Orientation.fromDegrees(170)
val MAX_BRANCH_OFFSET = Orientation.fromDegrees(20)

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

    fun validate(label: String) = when (this) {
        is NoBranching -> doNothing()
        is SimpleBranching -> {
            checkInt(maxCount, "${label}'s max branches", MIN_BRANCHES, MAX_BRANCHES)
            validateFactor(base, "${label}'s branching base", MIN_BRANCHING_BASE, MAX_BRANCHING_BASE)
            validateFactor(maxLength, "${label}'s max branch length", MIN_BRANCH_LENGTH, MAX_BRANCH_LENGTH)
            branch.validate("${label}'s branch")
            angle.validate("${label}'s branch angle", MIN_BRANCH_CENTER, MAX_BRANCH_CENTER, MAX_BRANCH_OFFSET)
        }
    }
}

@Serializable
@SerialName("None")
data object NoBranching : Branching()

@Serializable
@SerialName("Simple")
data class SimpleBranching(
    val maxCount: Int = MIN_BRANCHES,
    val sidePattern: BranchSidePattern = BranchSidePattern.BothSides,
    val base: Factor = DEFAULT_BRANCHING_BASE,
    val maxLength: Factor = DEFAULT_BRANCH_LENGTH,
    val length: TreeSilhouetteShape = TreeSilhouetteShape.Conical,
    val branch: Stem = Stem(),
    /**
     * The angle between the parent and the children.
     */
    val angle: Variance<Orientation> = Variance(DEFAULT_BRANCH_CENTER),
) : Branching()
