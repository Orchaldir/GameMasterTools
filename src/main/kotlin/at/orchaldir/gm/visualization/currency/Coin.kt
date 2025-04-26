package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.HoledCoin

fun visualizeCoin(
    state: CurrencyRenderState,
    coin: Coin,
) {
    val options = state.getFillAndBorder(coin.material)

    visualizeShape(
        state.renderer.getLayer(),
        state.aabb.getCenter(),
        coin.shape,
        coin.radius,
        options
    )
}

fun visualizeHoledCoin(
    state: CurrencyRenderState,
    coin: HoledCoin,
) {
    val options = state.getFillAndBorder(coin.material)

    visualizeHoledShape(
        state.renderer.getLayer(),
        state.aabb.getCenter(),
        coin.shape,
        coin.radius,
        coin.holeShape,
        coin.radius * coin.holeFactor,
        options
    )
}
