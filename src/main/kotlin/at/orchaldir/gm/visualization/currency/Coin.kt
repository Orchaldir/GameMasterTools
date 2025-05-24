package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly

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
    val innerAabb = coin.shape.calculateInnerAabb(aabb, UsingCircularShape(CircularShape.Circle), FULL)
    visualizeCoinSide(state, renderer, innerAabb, coin.front)
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
    visualizeOuterRim(state, renderer, aabb, coin.shape, rimWidth)

    if (coin.hasHoleRim) {
        visualizeInnerRim(
            state,
            renderer,
            holeAabb,
            coin.holeShape,
            rimWidth,
        )
    }
    /*
        visualizeHoledCoinSide(
            state,
            renderer,
            center,
            coin.calculateInnerShapeRadius(CircularShape.Circle),
            holeRadius,
            coin.front,
        )
     */
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

    //visualizeCoinSide(state, renderer, center, coin.calculateInnerShapeRadius(CircularShape.Circle), coin.front)
}

private fun visualizeOuterRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    shape: ComplexShape,
    rimWidth: Distance,
) {
    if (rimWidth == ZERO_DISTANCE) {
        return
    }

    val rimAabb = aabb.shrink(rimWidth)

    visualizeRim(state, renderer, rimAabb, shape)
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
