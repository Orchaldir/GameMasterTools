package at.orchaldir.gm.prototypes.visualization.currency

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.currency.ResolvedCurrencyData

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
        "coin-name.svg",
        State(materialStorage),
        CURRENCY_CONFIG,
        CURRENCY_CONFIG.calculatePaddedCoinSize(radius),
        addNames(Shape.entries),
        addNames(listOf("D", "DD", "DDD", "DDDD", "DDDDD")),
        { _, name ->
            ResolvedCurrencyData(Name.init(name))
        }
    ) { shape, name ->
        Coin(
            gold,
            shape,
            radius,
            DEFAULT_THICKNESS,
            DEFAULT_RIM_FACTOR,
            ShowName(),
        )
    }
}