package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.NoBorder

data class ResolvedCurrencyData(
    val name: Name = Name.init("Unknown"),
    val value: Int = 1,
)

data class CurrencyRenderState(
    val state: State,
    val aabb: AABB,
    val config: CurrencyRenderConfig,
    val renderer: MultiLayerRenderer,
) {

    fun getFillAndBorder(material: MaterialId) =
        FillAndBorder(color(material).toRender(), config.line)

    fun getNoBorder(material: MaterialId) =
        NoBorder(color(material).toRender())

    private fun color(material: MaterialId): Color =
        state.getMaterialStorage().get(material)?.color ?: Color.Pink
}