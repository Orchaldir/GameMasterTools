package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class ComplexLifeStage(
    val name: String,
    val maxAge: Int,
    val relativeSize: Factor = FULL,
    val appearance: RaceAppearanceId = RaceAppearanceId(0),
) : LifeStage {
    override fun name() = name
    override fun maxAge() = maxAge
    override fun relativeSize() = relativeSize
}
