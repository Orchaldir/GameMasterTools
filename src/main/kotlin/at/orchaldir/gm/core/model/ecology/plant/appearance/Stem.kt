package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.checkInt
import kotlinx.serialization.Serializable

const val MIN_SEGMENTS = 3
const val MAX_SEGMENTS = 100

@Serializable
data class Stem(
    val segments: Int = 3,
    val shape: StemShape = StraightStem,
    val thickness: StemThickness = LinearStemThickness(),
    val splitting: StemSplitting = NoStemSplitting,
    val branching: Branching = NoBranching,
) {

    fun validate(label: String) {
        checkInt(segments, "${label}'s number of segments", MIN_SEGMENTS, MAX_SEGMENTS)
        shape.validate(label)
        thickness.validate(label)
        splitting.validate(label)
    }

}
