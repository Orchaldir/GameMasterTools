package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val radius = Distance.fromCentimeters(1)
    val gold = MaterialId(0)
    val silver = MaterialId(1)
    val materialStorage = Storage(
        listOf(
            Material(gold, color = Color.Gold),
            Material(silver, color = Color.Silver),
        )
    )

    renderCurrencyTable(
        "coin-shapes.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        addNames(CircularShape.entries),
        addNames(listOf(null, CircularShape.Circle, CircularShape.Square, CircularShape.Octagon)),
    ) { shape, hole ->
        if (hole == null) {
            Coin(
                gold,
                shape,
                radius,
            )
        } else {
            val front = HoledCoinSide(
                ShowValue(),
                ShowNumber(),
                ShowDenomination(),
                ShowName(),
            )
            HoledCoin(
                gold,
                shape,
                radius,
                DEFAULT_THICKNESS,
                DEFAULT_RIM_FACTOR,
                hole,
                front = front
            )
        }

    }
}