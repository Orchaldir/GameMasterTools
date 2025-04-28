package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BlankCoinSide
import at.orchaldir.gm.core.model.economy.money.CoinSide
import at.orchaldir.gm.core.model.economy.money.ShowDenomination
import at.orchaldir.gm.core.model.economy.money.ShowName
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer

fun visualizeCoinSide(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: CoinSide,
) = when (side) {
    BlankCoinSide -> doNothing()
    is ShowDenomination -> visualizeDenomination(state, renderer, center, radius, side)
    is ShowName -> doNothing()
}

private fun visualizeDenomination(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowDenomination,
) {

}
