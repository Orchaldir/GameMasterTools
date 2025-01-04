package at.orchaldir.gm.visualization.book

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class BookRenderState(
    val aabb: AABB,
    val config: BookRenderConfig,
    val renderer: MultiLayerRenderer,
    val renderFront: Boolean,
)