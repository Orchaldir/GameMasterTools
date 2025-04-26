package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Circle
import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.Square
import at.orchaldir.gm.core.model.economy.money.Triangle
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val radius = Distance.fromCentimeters(1)
    val shapes = listOf(
        Pair("Circle", Circle),
        Pair("Triangle", Triangle()),
        Pair("Rounded Triangle", Triangle(true)),
        Pair("Square", Square()),
        Pair("Rounded Square", Square(true)),
    )
    val gold = MaterialId(0)
    val silver = MaterialId(1)
    val materialStorage = Storage(
        listOf(
            Material(gold, color = Color.Gold),
            Material(silver, color = Color.Silver),
        )
    )
    val materials = listOf(
        Pair("Gold", gold),
        Pair("Silver", silver),
    )

    renderCurrencyTable(
        "coin-shapes.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        shapes,
        materials,
    ) { shape, material ->
        Coin(
            material,
            shape,
            radius,
        )
    }
}