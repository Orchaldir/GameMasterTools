package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import kotlinx.serialization.Serializable

@Serializable
data class Trunk(
    val length: Distribution<Distance>,
    val stem: Stem,
)
