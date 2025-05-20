package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.HUNDRED_µM
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import kotlin.math.pow

fun visualizeHoledCoinSide(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    holeRadius: Distance,
    side: HoledCoinSide,
) {
    val subRadius = radius - holeRadius
    val subHalf = subRadius / 2.0f
    val offset = holeRadius + subHalf

    visualizeHoledCoinSide(state, renderer, center.minusHeight(offset), subRadius, side.top)
    visualizeHoledCoinSide(state, renderer, center.minusWidth(offset), subHalf, side.left)
    visualizeHoledCoinSide(state, renderer, center.addWidth(offset), subHalf, side.right)
    visualizeHoledCoinSide(state, renderer, center.addHeight(offset), subRadius, side.bottom)
}

private fun visualizeHoledCoinSide(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: CoinSide,
) {
    if (side != BlankCoinSide) {
        visualizeCoinSide(
            state,
            renderer,
            center,
            radius,
            side,
        )
    }
}

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
    is ShowNumber -> visualizeNumber(state, renderer, center, radius, side)
    is ShowValue -> visualizeValue(state, renderer, center, radius, side)
}

private fun visualizeDenomination(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowDenomination,
) {
    val text = state.data.denomination.text.text
    visualizeText(state, renderer, center, side.font, text, radius)
}

private fun visualizeName(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowName,
) {
    val text = state.data.name.text
    visualizeText(state, renderer, center, side.font, text, radius)
}

private fun visualizeNumber(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowNumber,
) {
    val text = state.data.number.toString()
    visualizeText(state, renderer, center, side.font, text, radius)
}

private fun visualizeValue(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    side: ShowValue,
) {
    val text = state.data.denomination.display(state.data.number)
    visualizeText(state, renderer, center, side.font, text, radius)
}

private fun visualizeText(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    font: FontId?,
    text: String,
    size: Distance,
) {
    val factor = 0.75.pow((text.length - 2).toDouble()).toFloat()
    val options = RenderStringOptions(
        BorderOnly(LineOptions(Color.Black.toRender(), HUNDRED_µM)),
        size * factor,
        state.state.getFontStorage().getOptional(font),
    )

    renderer.renderString(text, center, ZERO_ORIENTATION, options)
}
