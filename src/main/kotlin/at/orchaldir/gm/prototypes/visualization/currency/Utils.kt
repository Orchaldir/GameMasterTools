package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyFormat
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.RectangularShape.*
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape
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

fun createExampleShapes(): MutableList<ComplexShape> {
    val shapes = CircularShape.entries
        .map { UsingCircularShape(it) }
        .toMutableList<ComplexShape>()
    listOf(Rectangle, Ellipse, Teardrop, ReverseTeardrop).forEach { shape ->
        shapes.add(UsingRectangularShape(shape))
    }

    return shapes
}

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
