package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
import kotlinx.serialization.Serializable

@Serializable
data class ComplexLifeStage(
    val name: String,
    val maxAge: Int?,
    val appearance: AppearanceOptions = AppearanceOptions(),
)
