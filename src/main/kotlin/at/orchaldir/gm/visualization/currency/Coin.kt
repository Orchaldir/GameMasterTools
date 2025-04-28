package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.core.model.economy.money.Shape
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.Point2d
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
    visualizeCoinRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)
}

fun visualizeHoledCoin(
    state: CurrencyRenderState,
    coin: HoledCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()

    visualizeHoledShape(
        renderer,
        center,
        coin.shape,
        coin.radius,
        coin.holeShape,
        coin.calculateHoleRadius(),
        options
    )
    visualizeCoinRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)
}

fun visualizeBiMetallicCoin(
    state: CurrencyRenderState,
    coin: BiMetallicCoin,
) {
    val options = state.getFillAndBorder(coin.material)
    val innerOptions = state.getFillAndBorder(coin.innerMaterial)
    val renderer = state.renderer.getLayer()
    val center = state.aabb.getCenter()

    visualizeShape(
        renderer,
        center,
        coin.shape,
        coin.radius,
        options
    )

    visualizeCoinRim(state, renderer, center, coin.shape, coin.radius, coin.rimFactor)

    visualizeShape(
        renderer,
        center,
        coin.innerShape,
        coin.calculateInnerRadius(),
        innerOptions
    )
}

private fun visualizeCoinRim(
    state: CurrencyRenderState,
    renderer: LayerRenderer,
    center: Point2d,
    shape: Shape,
    radius: Distance,
    rimFactor: Factor,
) {
    val rimRadius = radius * (ONE - rimFactor)
    val rimOptions = BorderOnly(state.config.line)

    visualizeShape(
        renderer,
        center,
        shape,
        rimRadius,
        rimOptions,
    )
}
