package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyFormat
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.currency.CurrencyRenderConfig
import at.orchaldir.gm.visualization.currency.CurrencyRenderState
import at.orchaldir.gm.visualization.currency.ResolvedCurrencyData
import at.orchaldir.gm.visualization.currency.visualizeCurrencyFormat

val CURRENCY_CONFIG = CurrencyRenderConfig(
    LineOptions(Color.Black.toRender(), fromMicrometers(200)),
    fromPercentage(200),
)

fun <C, R> renderCurrencyTable(
    filename: String,
    state: State,
    config: CurrencyRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    createData: (C, R) -> ResolvedCurrencyData,
    createFormat: (C, R) -> CurrencyFormat,
) {
    renderTable(filename, size, rows, columns, false) { aabb, renderer, _, column, row ->
        val data = createData(column, row)
        val format = createFormat(column, row)
        val state = CurrencyRenderState(state, aabb, config, renderer, data)

        visualizeCurrencyFormat(state, format)
    }
}


fun <C, R> renderCurrencyTable(
    filename: String,
    state: State,
    config: CurrencyRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    data: ResolvedCurrencyData = ResolvedCurrencyData(),
    create: (C, R) -> CurrencyFormat,
) {
    renderTable(filename, size, rows, columns, false) { aabb, renderer, _, column, row ->
        val format = create(column, row)
        val state = CurrencyRenderState(state, aabb, config, renderer, data)

        visualizeCurrencyFormat(state, format)
    }
}
