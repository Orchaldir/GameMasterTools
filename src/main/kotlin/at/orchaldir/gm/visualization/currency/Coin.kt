package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.utils.math.shape.Shape
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.BorderOnly

fun visualizeCoin(
    state: CurrencyRenderState,
    coin: Coin,
) {
    val options = state.getFillAndBorder(coin.material)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()

    visualizeShape(
        renderer,
        center,
        coin.shape,
        coin.radius,
        options
    )
    visualizeOuterRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)
    visualizeCoinSide(state, renderer, center, coin.calculateInnerShapeRadius(Shape.Circle), coin.front)
}

fun visualizeHoledCoin(
    state: CurrencyRenderState,
    coin: HoledCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()
    val holeRadius = coin.calculateHoleRadius()

    visualizeHoledShape(
        renderer,
        center,
        coin.shape,
        coin.radius,
        coin.holeShape,
        holeRadius,
        options
    )
    visualizeOuterRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)

    if (coin.hasHoleRim) {
        visualizeInnerRim(
            state,
            renderer,
            center,
            coin.radius,
            coin.holeShape,
            holeRadius,
            coin.rimFactor,
        )
    }

    visualizeHoledCoinSide(
        state,
        renderer,
        center,
        coin.calculateInnerShapeRadius(Shape.Circle),
        holeRadius,
        coin.front,
    )
}

fun visualizeBiMetallicCoin(
    state: CurrencyRenderState,
    coin: BiMetallicCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val innerOptions = state.getNoBorder(coin.innerMaterial)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()

    visualizeShape(
        renderer,
        center,
        coin.shape,
        coin.radius,
        options
    )

    visualizeOuterRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)

    visualizeShape(
        renderer,
        center,
        coin.innerShape,
        coin.calculateInnerRadius(),
        innerOptions
    )

    visualizeCoinSide(state, renderer, center, coin.calculateInnerShapeRadius(Shape.Circle), coin.front)
}

private fun visualizeOuterRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
    radius: Distance,
    rimFactor: Factor,
) {
    if (rimFactor == ZERO) {
        return
    }

    val rimRadius = radius * (ONE - rimFactor)
    visualizeRim(state, renderer, center, shape, rimRadius)
}

private fun visualizeInnerRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    radius: Distance,
    innerShape: Shape,
    innerRadius: Distance,
    rimFactor: Factor,
) {
    if (rimFactor == ZERO) {
        return
    }

    val rimRadius = innerRadius + radius * rimFactor
    visualizeRim(state, renderer, center, innerShape, rimRadius)
}

private fun visualizeRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
    rimRadius: Distance,
) {
    val rimOptions = BorderOnly(state.config.line)

    visualizeShape(
        renderer,
        center,
        shape,
        rimRadius,
        rimOptions,
    )
}
