package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.Serializable

@Serializable
data class Horn(
    val length: Factor,
    val width: Factor,
    val position: HornPosition,
    val orientationOffset: Orientation,
    val shape: HornShape,
    val color: Color,
) {

    fun calculatePadding(headHeight: Distance) = headHeight * length

}
