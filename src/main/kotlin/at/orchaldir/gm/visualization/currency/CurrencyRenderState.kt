package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.NoBorder

data class ResolvedCurrencyData(
    val name: Name = Name.init("Dollar"),
    val number: Int = 1,
    val denomination: Denomination = Denomination.init("gp", hasSpace = true),
)

data class CurrencyRenderState(
    val state: State,
    val aabb: AABB,
    val config: CurrencyRenderConfig,
    val renderer: MultiLayerRenderer,
    val data: ResolvedCurrencyData = ResolvedCurrencyData(),
) {

    fun getFillAndBorder(material: MaterialId) =
        FillAndBorder(color(material).toRender(), config.line)

    fun getNoBorder(material: MaterialId) =
        NoBorder(color(material).toRender())

    private fun color(material: MaterialId): Color =
        state.getMaterialStorage().get(material)?.color ?: Color.Pink
}