package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderOptions

interface RenderState {

    fun state(): State
    fun renderer(): MultiLayerRenderer


    fun lineOptions(): LineOptions
    fun lineOptions(part: ItemPart, width: Distance) =
        LineOptions(getColor(part).toRender(), width)

    fun getColor(part: ItemPart): Color

    fun getFillAndBorder(part: ItemPart, clipping: String? = null) =
        getFillAndBorder(part, lineOptions(), clipping)
    fun getFillAndBorder(part: ItemPart, lineOptions: LineOptions, clipping: String? = null): RenderOptions
    fun getNoBorder(part: ItemPart, clipping: String? = null): RenderOptions

}
