package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions

interface RenderState {

    fun state(): State
    fun renderer(): MultiLayerRenderer
    fun lineOptions(): LineOptions

}
