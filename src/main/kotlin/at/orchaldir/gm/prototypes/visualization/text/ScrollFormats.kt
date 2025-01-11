package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegmentShape.RoundedCylinder
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d

private val ID = MaterialId(0)

fun main() {
    val rollLength = Distance(200)
    val rollDiameter = Distance(50)
    val handle0 = ScrollHandle(HandleSegment(Distance(40), Distance(15), Color.SaddleBrown))
    val handle1 = ScrollHandle(
        listOf(
            HandleSegment(Distance(40), Distance(15), Color.Gold),
            HandleSegment(Distance(15), Distance(40), Color.Gold, RoundedCylinder),
        )
    )

    renderTextTable(
        "scroll-formats.svg",
        TEXT_CONFIG,
        Size2d(rollDiameter * 4, rollLength + Distance(200)),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(ScrollFormatType.entries),
    ) { color, type ->
        Scroll(
            when (type) {
                ScrollFormatType.NoRod -> ScrollWithoutRod
                ScrollFormatType.OneRod -> ScrollWithOneRod(handle0)
                ScrollFormatType.TwoRods -> ScrollWithTwoRods(handle1)
            },
            rollLength,
            rollDiameter,
            color
        )
    }
}