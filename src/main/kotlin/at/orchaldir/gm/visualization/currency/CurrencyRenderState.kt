package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder

data class CurrencyRenderState(
    val state: State,
    val aabb: AABB,
    val config: CurrencyRenderConfig,
    val renderer: MultiLayerRenderer,
) {

    fun getFillAndBorder(material: MaterialId): FillAndBorder {
        val color = state.getMaterialStorage().get(material)?.color ?: Color.Pink

        return FillAndBorder(color.toRender(), config.line)
    }
}