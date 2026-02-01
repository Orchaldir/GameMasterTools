package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.BiMetallicCoin
import at.orchaldir.gm.core.model.economy.money.DEFAULT_RIM_FACTOR
import at.orchaldir.gm.core.model.economy.money.DEFAULT_THICKNESS
import at.orchaldir.gm.core.model.economy.money.ShowValue
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
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

    renderCurrencyTable(
        "coin-bimetallic.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        addNames(CircularShape.entries),
        addNames(listOf(CircularShape.Circle, CircularShape.Triangle, CircularShape.Square)),
    ) { shape, innerShape ->
        BiMetallicCoin(
            gold,
            silver,
            UsingCircularShape(shape),
            radius,
            DEFAULT_THICKNESS,
            DEFAULT_RIM_FACTOR,
            UsingCircularShape(innerShape),
            fromPercentage(40),
            ShowValue(),
        )

    }
}