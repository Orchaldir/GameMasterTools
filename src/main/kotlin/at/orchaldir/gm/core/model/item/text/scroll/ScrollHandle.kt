package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.Serializable

@Serializable
data class ScrollHandle(
    val length: Distance,
    val diameter: Distance,
    val color: Color = Color.Blue,
    val material: MaterialId = MaterialId(0),
) {

    fun calculateLength(rollLength: Distance) = rollLength + length * 2

    fun calculateDiameter(rollDiameter: Distance) = rollDiameter.max(diameter)

    fun calculateSize() = Size2d(diameter, length)

}