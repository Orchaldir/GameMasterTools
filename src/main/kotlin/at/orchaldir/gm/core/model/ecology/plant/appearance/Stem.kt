package at.orchaldir.gm.core.model.ecology.plant.appearance

import kotlinx.serialization.Serializable

const val MIN_SEGMENTS = 3
const val MAX_SEGMENTS = 100

@Serializable
data class Stem(
    val segments: Int = 3,
    val shape: StemShape = StraightStem,
    val thickness: StemThickness = LinearStemThickness(),
    val splitting: StemSplitting = NoStemSplitting,
)
