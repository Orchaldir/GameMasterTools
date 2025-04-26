package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.Coin

fun visualizeCoin(
    state: CurrencyRenderState,
    coin: Coin,
) {
    val options = state.getFillAndBorder(coin.material)

    visualizeShape(
        state.renderer.getLayer(),
        state.aabb.getCenter(),
        coin.radius,
        coin.shape,
        options
    )
}
