package at.orchaldir.gm.core.model.race.aging

import kotlinx.serialization.Serializable

@Serializable
data class SimpleAgeCategory(
    val name: String,
    val maxAge: Int?,
)
