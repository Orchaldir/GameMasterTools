package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import kotlinx.serialization.Serializable

val MIN_TRUNK_HEIGHT = Distance.fromMeters(1)
val MAX_TRUNK_HEIGHT = Distance.fromMeters(200)

@Serializable
data class Trunk(
    val height: Distribution<Distance> = Distribution(MIN_TRUNK_HEIGHT),
    val stem: Stem = Stem(),
    val bark: Color = Color.SaddleBrown,
)
