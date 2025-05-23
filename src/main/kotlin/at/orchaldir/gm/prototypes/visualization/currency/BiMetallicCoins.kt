package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.shape.Shape
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
            DEFAULT_THICKNESS,
            DEFAULT_RIM_FACTOR,
            silver,
            innerShape,
            fromPercentage(40),
            ShowValue(),
        )

    }
}