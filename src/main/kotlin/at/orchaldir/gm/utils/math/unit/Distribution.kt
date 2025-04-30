package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class Distribution<T : SiUnit<T>>(
    val center: T,
    val offset: T,
) {
    companion object {
        fun fromMeters(center: Float, offset: Float) =
            Distribution(Distance.fromMeters(center), Distance.fromMeters(offset))

        fun fromKilograms(center: Float, offset: Float) =
            Distribution(Weight.fromKilograms(center), Weight.fromKilograms(offset))
    }

    fun getMin() = center - offset
    fun getMax() = center + offset

    fun display() = String.format("%s +- %s", center, offset)

    fun isInside(distance: T) = (distance.value() - center.value()).absoluteValue < offset.value()
}
