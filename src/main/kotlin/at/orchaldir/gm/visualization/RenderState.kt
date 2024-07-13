package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.Renderer

data class RenderState(
    val aabb: AABB,
    val config: RenderConfig,
    val renderer: Renderer,
    val renderFront: Boolean,
)
