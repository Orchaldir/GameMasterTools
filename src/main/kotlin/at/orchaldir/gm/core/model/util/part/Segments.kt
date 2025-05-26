package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.utils.math.maxOf
import at.orchaldir.gm.utils.math.sumOf
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Segments(
    val segments: List<Segment>,
) : MadeFromParts {
    constructor(segment: Segment) : this(listOf(segment))

    fun calculateLengthFactor() = sumOf(segments.map { it.length })

    fun calculateDiameterFactor() = maxOf(segments.map { it.diameter })

    fun calculateLength(baseLength: Distance) = baseLength * calculateLengthFactor()

    fun calculateDiameter(baseDiameter: Distance) = baseDiameter * calculateDiameterFactor()

    override fun parts() = segments.fold(listOf<ItemPart>()) { sum, segment ->
        sum + segment.parts()
    }
}