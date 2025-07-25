package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
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
    aabb: AABB,
    holeAABB: AABB,
    side: HoledCoinSide,
) {
    val center = aabb.getCenter()
    val radius = aabb.getInnerRadius()
    val holeHalfWidth = holeAABB.size.width / 2
    val holeHalfHeight = holeAABB.size.height / 2
    val subWidth = radius - holeHalfWidth
    val subHeight = radius - holeHalfHeight
    val offsetWidth = holeHalfWidth + subWidth / 2
    val offsetHeight = holeHalfHeight + subHeight / 2

    visualizeHoledCoinSide(state, renderer, center.minusHeight(offsetHeight), subHeight, side.top)
    visualizeHoledCoinSide(state, renderer, center.minusWidth(offsetWidth), subWidth, side.left)
    visualizeHoledCoinSide(state, renderer, center.addWidth(offsetWidth), subWidth, side.right)
    visualizeHoledCoinSide(state, renderer, center.addHeight(offsetHeight), subHeight, side.bottom)
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
    aabb: AABB,
    side: CoinSide,
) = visualizeCoinSide(state, renderer, aabb.getCenter(), aabb.getInnerRadius(), side)

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
