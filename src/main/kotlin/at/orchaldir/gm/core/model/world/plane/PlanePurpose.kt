package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PlanePurposeType {
    Demi,
    Heart,
    Independent,
    Reflective;
}

@Serializable
sealed class PlanePurpose {

    fun getType() = when (this) {
        is Demiplane -> PlanePurposeType.Demi
        is HeartPlane -> PlanePurposeType.Heart
        is IndependentPlane -> PlanePurposeType.Independent
        is ReflectivePlane -> PlanePurposeType.Reflective
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
data object IndependentPlane : PlanePurpose()

@Serializable
@SerialName("Reflective")
data class ReflectivePlane(val plane: PlaneId) : PlanePurpose()



