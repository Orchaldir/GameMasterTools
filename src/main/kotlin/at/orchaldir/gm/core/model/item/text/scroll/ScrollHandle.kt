package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.sumOf
import kotlinx.serialization.Serializable

@Serializable
data class ScrollHandle(
    val segments: List<HandleSegment>,
) : MadeFromParts {
    constructor(segment: HandleSegment) : this(listOf(segment))

    fun calculateHandleLength() = sumOf(segments.map { it.length })

    fun calculateHandleDiameter() = sumOf(segments.map { it.diameter })

    fun calculateLength(rollLength: Distance) = rollLength + calculateHandleLength() * 2

    fun calculateDiameter(rollDiameter: Distance) = rollDiameter.max(calculateHandleDiameter())

    override fun parts() = segments.fold(listOf<ItemPart>()) { sum, segment ->
        sum + segment.parts()
    }
}