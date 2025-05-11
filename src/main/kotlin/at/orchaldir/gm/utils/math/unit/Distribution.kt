package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.core.model.race.MAX_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.MAX_RACE_HEIGHT_OFFSET
import at.orchaldir.gm.core.model.race.MIN_RACE_HEIGHT
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
data class Distribution<T : SiUnit<T>>(
    val center: T,
    val offset: T,
) {
    init {
        require(center.value() > offset.value()) { "Center must be greater than the offset!" }
    }

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
