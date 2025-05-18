package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PlanePurposeType {
    Demi,
    Heart,
    Independent,
    Material,
    Prison,
    Reflective;
}

@Serializable
sealed class PlanePurpose : Creation {

    fun getType() = when (this) {
        is Demiplane -> PlanePurposeType.Demi
        is HeartPlane -> PlanePurposeType.Heart
        is IndependentPlane -> PlanePurposeType.Independent
        MaterialPlane -> PlanePurposeType.Material
        is PrisonPlane -> PlanePurposeType.Prison
        is ReflectivePlane -> PlanePurposeType.Reflective
    }

    override fun creator() = if (this is PrisonPlane) {
        creator
    } else {
        UndefinedCreator
    }
}

@Serializable
@SerialName("Demiplane")
data class Demiplane(val plane: PlaneId) : PlanePurpose()


@Serializable
@SerialName("Heart")
data class HeartPlane(val god: GodId) : PlanePurpose()

@Serializable
@SerialName("Independent")
data class IndependentPlane(
    val pattern: PlaneAlignmentPattern = RandomAlignment,
) : PlanePurpose()

@Serializable
@SerialName("Material")
data object MaterialPlane : PlanePurpose()

@Serializable
@SerialName("Prison")
data class PrisonPlane(
    val gods: Set<GodId>,
    val creator: Creator = UndefinedCreator,
) : PlanePurpose()

@Serializable
@SerialName("Reflective")
data class ReflectivePlane(val plane: PlaneId) : PlanePurpose()



