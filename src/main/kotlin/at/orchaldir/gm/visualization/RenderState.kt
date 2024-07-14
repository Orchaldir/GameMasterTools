package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.character.ABOVE_EQUIPMENT_LAYER

data class RenderState(
    val aabb: AABB,
    val config: RenderConfig,
    val renderer: Renderer,
    val renderFront: Boolean,
) {

    fun getBeardLayer() = if (renderFront) {
        ABOVE_EQUIPMENT_LAYER
    } else {
        -ABOVE_EQUIPMENT_LAYER
    }
}
