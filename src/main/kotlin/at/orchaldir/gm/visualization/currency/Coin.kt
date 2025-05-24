package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.LineOptions

fun visualizeCoin(
    state: CurrencyRenderState,
    coin: Coin,
) {
    val options = state.getFillAndBorder(coin.material)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()
    val aabb = coin.shape.calculateAabb(center, coin.radius)

    visualizeComplexShape(
        renderer,
        aabb,
        coin.shape,
        options
    )
    visualizeOuterRim(state, renderer, aabb, coin.shape, coin.radius * coin.rimFactor)
    val sideAabb = coin.shape.calculateInnerAabb(aabb, UsingCircularShape(CircularShape.Circle), FULL)
    visualizeCoinSide(state, renderer, sideAabb, coin.front)
}

fun visualizeHoledCoin(
    state: CurrencyRenderState,
    coin: HoledCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()
    val aabb = coin.shape.calculateAabb(center, coin.radius)
    val holeAabb = coin.calculateInnerAabb(aabb)
    val rimWidth = coin.radius * coin.rimFactor

    visualizeHoledComplexShape(
        renderer,
        aabb,
        coin.shape,
        holeAabb,
        coin.holeShape,
        options
    )

    val outerRimAabb = visualizeOuterRim(state, renderer, aabb, coin.shape, rimWidth)

    val holeRimAabb = if (coin.hasHoleRim) {
        visualizeInnerRim(
            state,
            renderer,
            holeAabb,
            coin.holeShape,
            rimWidth,
        )

        holeAabb.grow(rimWidth)
    } else {
        holeAabb
    }

    val sideAabb = coin.shape.calculateInnerAabb(outerRimAabb, UsingCircularShape(CircularShape.Circle), FULL)

    visualizeHoledCoinSide(
        state,
        renderer,
        sideAabb,
        holeRimAabb,
        coin.front,
    )

    debugHoledCoin(
        renderer,
        coin.shape,
        coin.holeShape,
        aabb,
        outerRimAabb,
        holeRimAabb,
        holeAabb,
        sideAabb,
    )
}

private fun debugHoledCoin(
    renderer: LayerRenderer,
    outer: ComplexShape,
    inner: ComplexShape,
    aabb: AABB,
    outerRimAabb: AABB,
    holeRimAabb: AABB,
    holeAabb: AABB,
    sideAabb: AABB,
) {
    val lineWidth = 0.0001f
    val options = BorderOnly(LineOptions(Color.Red.toRender(), lineWidth))
    val sideOptions = BorderOnly(LineOptions(Color.Green.toRender(), lineWidth))

    debugAabb(renderer, outer, aabb, options)
    debugAabb(renderer, outer, outerRimAabb, options)
    debugAabb(renderer, outer, sideAabb, sideOptions)
    debugAabb(renderer, inner, holeRimAabb, options)
    debugAabb(renderer, inner, holeAabb, options)
    renderer.renderCircle(holeAabb.getCenter(), Distance.fromMillimeters(0.1f), options)
}

private fun debugAabb(
    renderer: LayerRenderer,
    shape: ComplexShape,
    aabb: AABB,
    options: BorderOnly,
) {
    if (shape is UsingCircularShape) {
        renderer.renderCircle(aabb.getCenter(), aabb.getInnerRadius(), options)
    } else {
        renderer.renderRectangle(aabb, options)
    }
}

fun visualizeBiMetallicCoin(
    state: CurrencyRenderState,
    coin: BiMetallicCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val innerOptions = state.getNoBorder(coin.innerMaterial)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()
    val aabb = coin.shape.calculateAabb(center, coin.radius)
    val innerAabb = coin.calculateInnerAabb(aabb)

    visualizeComplexShape(
        renderer,
        aabb,
        coin.shape,
        options
    )

    visualizeOuterRim(state, renderer, aabb, coin.shape, coin.radius * coin.rimFactor)

    visualizeComplexShape(
        renderer,
        innerAabb,
        coin.innerShape,
        innerOptions
    )

    val sideAabb = coin.shape.calculateInnerAabb(aabb, UsingCircularShape(CircularShape.Circle), FULL)
    visualizeCoinSide(state, renderer, sideAabb, coin.front)
}

private fun visualizeOuterRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
    rimWidth: Distance,
): AABB {
    if (rimWidth == ZERO_DISTANCE) {
        return aabb
    }

    val rimAabb = aabb.shrink(rimWidth)

    visualizeRim(state, renderer, rimAabb, shape)

    return rimAabb
}

private fun visualizeInnerRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    innerAabb: AABB,
    innerShape: ComplexShape,
    rimWidth: Distance,
) {
    if (rimWidth == ZERO_DISTANCE) {
        return
    }

    val rimAabb = innerAabb.grow(rimWidth)

    visualizeRim(state, renderer, rimAabb, innerShape)
}

private fun visualizeRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
) {
    val rimOptions = BorderOnly(state.config.line)

    visualizeComplexShape(
        renderer,
        aabb,
        shape,
        rimOptions,
    )
}
