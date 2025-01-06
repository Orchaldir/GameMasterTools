package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d

private val ID = MaterialId(0)

fun main() {
    val rollLength = Distance(200)
    val rollDiameter = Distance(50)
    val rod = ScrollRod(rollDiameter, rollDiameter / 2.0f)

    renderTextTable(
        "scroll-formats.svg",
        TEXT_CONFIG,
        Size2d(rollDiameter * 4, rollLength + Distance(200)),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(ScrollFormatType.entries),
    ) { color, type ->
        Scroll(
            rollLength,
            rollDiameter,
            when (type) {
                ScrollFormatType.NoRod -> ScrollWithoutRod
                ScrollFormatType.OneRod -> ScrollWithOneRod(rod)
                ScrollFormatType.TwoRods -> ScrollWithTwoRods(rod)
            },
            color
        )
    }
}