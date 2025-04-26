package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class CurrencyRenderState(
    val state: State,
    val aabb: AABB,
    val config: CurrencyRenderConfig,
    val renderer: MultiLayerRenderer,
)