package at.orchaldir.gm.core.model.race.aging

import at.orchaldir.gm.core.model.race.appearance.AppearanceOptions
import kotlinx.serialization.Serializable

@Serializable
data class ComplexAgeCategory(
    val name: String,
    val maxAge: Int?,
    val appearance: AppearanceOptions = AppearanceOptions(),
)
