package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
data class ScrollHandle(
    val segments: List<HandleSegment>,
    val material: MaterialId = MaterialId(0),
) {
    constructor(segment: HandleSegment, material: MaterialId = MaterialId(0)) : this(listOf(segment), material)

    fun calculateHandleLength() = Distance(segments.sumOf { it.length.millimeters })

    fun calculateHandleDiameter() = Distance(segments.maxOf { it.diameter.millimeters })

    fun calculateLength(rollLength: Distance) = rollLength + calculateHandleLength() * 2

    fun calculateDiameter(rollDiameter: Distance) = rollDiameter.max(calculateHandleDiameter())

}