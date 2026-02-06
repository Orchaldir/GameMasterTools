package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val radius = Distance.fromCentimeters(1)
    val gold = MaterialId(0)
    val silver = MaterialId(1)
    val materialStorage = Storage(
        listOf(
            mockMaterial(Color.Gold, gold),
            mockMaterial(Color.Silver, silver),
        )
    )
    val holeShapes = mutableListOf<ComplexShape?>(null)
    holeShapes.addAll(
        listOf(CircularShape.Circle, CircularShape.Square, CircularShape.Octagon)
            .map { UsingCircularShape(it) })
    holeShapes.add(UsingRectangularShape(RectangularShape.Teardrop))

    renderCurrencyTable(
        "coin-shapes.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        addNames(createExampleShapes()),
        addNames(holeShapes),
    ) { shape, hole ->
        if (hole == null) {
            Coin(
                gold,
                shape,
                radius,
            )
        } else {
            val front = HoledCoinSide(
                ShowNumber(),
                ShowNumber(),
                ShowNumber(),
                ShowNumber(),
            )
            HoledCoin(
                gold,
                shape,
                radius,
                DEFAULT_THICKNESS,
                DEFAULT_RIM_FACTOR,
                hole,
                front = front,
            )
        }

    }
}