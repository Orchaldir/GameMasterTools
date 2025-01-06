package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

private val ID = MaterialId(0)

fun main() {
    val size = Size2i(125, 190)

    val rollLength = Distance(200)
    val rollDiameter = Distance(20)
    val rod = ScrollRod(rollDiameter, rollDiameter / 2.0f)

    renderTextTable(
        "scroll-formats.svg",
        TEXT_CONFIG,
        size.toSize2d() + Distance(50),
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