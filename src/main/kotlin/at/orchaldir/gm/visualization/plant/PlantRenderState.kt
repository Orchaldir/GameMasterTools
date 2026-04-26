package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class PlantRenderState(
    val state: State,
    val config: PlantRenderConfig,
    val renderer: MultiLayerRenderer,
)