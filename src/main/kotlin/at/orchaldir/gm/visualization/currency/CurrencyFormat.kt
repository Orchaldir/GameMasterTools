package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyFormat
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.economy.money.UndefinedCurrencyFormat
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.selector.item.getAuthorName
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.ResolvedTextData
import at.orchaldir.gm.visualization.text.book.visualizeBook
import at.orchaldir.gm.visualization.text.scroll.visualizeScroll

fun visualizeCurrencyUnit(
    state: State,
    config: CurrencyRenderConfig,
    unit: CurrencyUnit,
) = visualizeCurrencyUnit(state, config, unit, config.calculatePaddedSize(unit.format))

fun visualizeCurrencyUnit(
    state: State,
    config: CurrencyRenderConfig,
    unit: CurrencyUnit,
    size: Size2d,
): Svg {
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val renderState = CurrencyRenderState(state, aabb, config, builder)

    visualizeCurrencyFormat(renderState, unit.format)

    return builder.finish()
}

fun visualizeCurrencyFormat(
    state: CurrencyRenderState,
    format: CurrencyFormat,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), state.config.calculateSize(format))
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (format) {
        UndefinedCurrencyFormat -> doNothing()
        is Coin -> doNothing()
    }
}
