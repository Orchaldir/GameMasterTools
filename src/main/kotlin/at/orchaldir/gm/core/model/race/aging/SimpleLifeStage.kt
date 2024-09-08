package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class SimpleLifeStage(
    val name: String,
    val maxAge: Int,
    val relativeHeight: Factor,
) : LifeStage {
    override fun name() = name
    override fun maxAge() = maxAge
    override fun relativeHeight() = relativeHeight
}