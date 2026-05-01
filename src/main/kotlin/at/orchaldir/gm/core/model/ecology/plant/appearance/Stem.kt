package at.orchaldir.gm.core.model.ecology.plant.appearance

import kotlinx.serialization.Serializable

const val MIN_SEGMENTS = 3
const val MAX_SEGMENTS = 100

@Serializable
data class Stem(
    val segments: Int,
    val shape: StemShape,
    val thickness: StemThickness,
    val splitting: StemSplitting = NoStemSplitting,
)
