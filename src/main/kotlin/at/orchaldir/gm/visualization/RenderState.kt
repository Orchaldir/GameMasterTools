package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderOptions

interface RenderState {

    fun state(): State
    fun renderer(): MultiLayerRenderer
    fun lineOptions(): LineOptions

    fun getRenderOptions(part: ItemPart, clipping: String? = null): RenderOptions

}
