package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class ResolvedCurrencyData(
    val name: Name = Name.init("Dollar"),
    val number: Int = 1,
    val denomination: Denomination = Denomination.init("gp", hasSpace = true),
)

data class PlantRenderState(
    val state: State,
    val aabb: AABB,
    val config: PlantRenderConfig,
    val renderer: MultiLayerRenderer,
    val data: ResolvedCurrencyData = ResolvedCurrencyData(),
)