package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class TextRenderState(
    val aabb: AABB,
    val config: TextRenderConfig,
    val renderer: MultiLayerRenderer,
)