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
import kotlin.math.pow

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
    visualizeText(state, renderer, center, side.font, text, radius * 0.75f)
}

private fun visualizeName(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowName,
) {
    val text = state.data.name.text
    // 1 = 1.5
    // 2 = 1
    // 3 = 0.75
    // 4 = 0.5
    val factor = 0.75.pow((text.length - 2).toDouble()).toFloat()
    visualizeText(state, renderer, center, side.font, text, radius * factor)
}

private fun visualizeText(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    font: FontId?,
    text: String,
    size: Distance,
) {
    val options = RenderStringOptions(
        BorderOnly(LineOptions(Color.Black.toRender(), HUNDRED_µM)),
        size.toMeters(),
        state.state.getFontStorage().getOptional(font),
    )

    renderer.renderString(text, center, ZERO_ORIENTATION, options)
}
