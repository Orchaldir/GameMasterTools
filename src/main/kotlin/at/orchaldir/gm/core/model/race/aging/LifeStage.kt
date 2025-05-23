package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class LifeStage(
    val name: Name,
    val maxAge: Int,
    val relativeSize: Factor = FULL,
    val hasBeard: Boolean = false,
    val hairColor: Color? = null,
)
