package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.checkFactor
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

val MIN_DISTRIBUTION_FACTOR = ZERO
val MAX_DISTRIBUTION_FACTOR = HALF

@Serializable
data class Distribution<T : SiUnit<T>>(
    val center: T,
    val offset: Factor,
) {
    init {
        checkFactor(offset, "distribution offset", MIN_DISTRIBUTION_FACTOR, MAX_DISTRIBUTION_FACTOR)
    }

    companion object {
        fun fromMeters(center: Float, offset: Factor) =
            Distribution(Distance.fromMeters(center), offset)
    }

    fun getMin() = center * (FULL - offset)
    fun getMax() = center * (FULL + offset)

    fun display() = String.format("%s +- %s", center, offset)

    fun isInside(distance: T) =
        ((distance.value() - center.value()).absoluteValue) / center.value().toFloat() < offset.toNumber()
}
