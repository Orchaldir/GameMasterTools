package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.DEFAULT_RIM_FACTOR
import at.orchaldir.gm.core.model.economy.money.Shape
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
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
        "coin-bimetallic.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        addNames(Shape.entries),
        addNames(listOf(Shape.Circle, Shape.Triangle, Shape.Square)),
    ) { shape, innerShape ->
        BiMetallicCoin(
            gold,
            shape,
            radius,
            DEFAULT_RIM_FACTOR,
            silver,
            innerShape,
            Factor.fromPercentage(40),
        )

    }
}