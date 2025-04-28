package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BlankCoinSide
import at.orchaldir.gm.core.model.economy.money.CoinSide
import at.orchaldir.gm.core.model.economy.money.ShowDenomination
import at.orchaldir.gm.core.model.economy.money.ShowName
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO_ORIENTATION
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.HUNDRED_µM
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
    is ShowName -> visualizeName(state, renderer, center, radius, side)
}

private fun visualizeDenomination(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowDenomination,
) {
    val text = state.data.denomination.display(state.data.number)
    visualizeText(state, renderer, center, radius, side.font, text)
}

private fun visualizeName(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowName,
) = visualizeText(state, renderer, center, radius, side.font, state.data.name.text)

private fun visualizeText(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    font: FontId?,
    text: String,
) {
    val size = radius * 0.75f
    val options = RenderStringOptions(
        BorderOnly(LineOptions(Color.Black.toRender(), HUNDRED_µM)),
        size.toMeters(),
        state.state.getFontStorage().getOptional(font),
    )

    renderer.renderString(text, center, ZERO_ORIENTATION, options)
}
