package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.sumOf
import kotlinx.serialization.Serializable

@Serializable
data class ScrollHandle(
    val segments: List<HandleSegment>,
    val material: MaterialId = MaterialId(0),
) {
    constructor(segment: HandleSegment, material: MaterialId = MaterialId(0)) : this(listOf(segment), material)

    fun calculateHandleLength() = sumOf(segments.map { it.length })

    fun calculateHandleDiameter() = sumOf(segments.map { it.diameter })

    fun calculateLength(rollLength: Distance) = rollLength + calculateHandleLength() * 2

    fun calculateDiameter(rollDiameter: Distance) = rollDiameter.max(calculateHandleDiameter())

}