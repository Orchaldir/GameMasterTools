package at.orchaldir.gm.core.model.ecology.plant.appearance

import kotlinx.serialization.Serializable

@Serializable
data class Stem(
    val segments: Int,
    val shape: StemShape,
    val splitting: StemSplitting,
    val thickness: StemThickness,
)
