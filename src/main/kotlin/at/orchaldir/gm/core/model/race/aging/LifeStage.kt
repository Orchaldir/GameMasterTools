package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class LifeStage(
    val name: String,
    val maxAge: Int,
    val relativeSize: Factor = FULL,
    val hasBeard: Boolean = false,
)
