package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.Serializable

@Serializable
data class HandleSegment(
    val length: Distance,
    val diameter: Distance,
    val color: Color = Color.Blue,
) {

    fun calculateSize() = Size2d(diameter, length)

}