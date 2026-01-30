package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.*
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

val MIN_DISTRIBUTION_FACTOR = ZERO
val DEFAULT_DISTRIBUTION_FACTOR = Factor.fromPercentage(20)
val MAX_DISTRIBUTION_FACTOR = HALF

@Serializable
data class Distribution<T : SiUnit<T>>(
    val center: T,
    val offset: Factor = DEFAULT_DISTRIBUTION_FACTOR,
) {
    init {
        validateFactor(offset, "distribution offset", MIN_DISTRIBUTION_FACTOR, MAX_DISTRIBUTION_FACTOR)
    }

    companion object {
        fun fromMeters(center: Float, offset: Factor = DEFAULT_DISTRIBUTION_FACTOR) =
            Distribution(Distance.fromMeters(center), offset)
    }

    fun getMin() = center * (FULL - offset)
    fun getMax() = center * (FULL + offset)

    fun display() = String.format("%s +- %s", center, offset)

    fun isInside(distance: T) =
        ((distance.value() - center.value()).absoluteValue) / center.value().toFloat() < offset.toNumber()
}
