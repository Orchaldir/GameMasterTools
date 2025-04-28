package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BlankCoinSide
import at.orchaldir.gm.core.model.economy.money.CoinSide
import at.orchaldir.gm.core.model.economy.money.ShowDenomination
import at.orchaldir.gm.core.model.economy.money.ShowName
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO_ORIENTATION
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

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
    val text = state.data.denomination.display(state.data.number)
    val options = RenderStringOptions(
        BorderOnly(LineOptions(Color.Black.toRender(), radius / 50.0f)),
        radius.toMeters(),
        state.state.getFontStorage().getOptional(side.font),
    )

    renderer.renderString(text, center, ZERO_ORIENTATION, options)
}
