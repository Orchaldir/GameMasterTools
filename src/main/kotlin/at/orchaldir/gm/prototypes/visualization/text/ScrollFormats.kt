package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegmentShape.RoundedCylinder
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val rollLength = fromMillimeters(200)
    val rollDiameter = fromMillimeters(50)
    val handle0 = ScrollHandle(HandleSegment(fromMillimeters(40), fromMillimeters(15), Color.SaddleBrown))
    val handle1 = ScrollHandle(
        listOf(
            HandleSegment(fromMillimeters(40), fromMillimeters(15), Color.Gold),
            HandleSegment(fromMillimeters(15), fromMillimeters(40), Color.Gold, RoundedCylinder),
        )
    )

    renderTextFormatTable(
        "scroll-formats.svg",
        State(),
        TEXT_CONFIG,
        Size2d(rollDiameter * 4, rollLength + fromMillimeters(200)),
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
            main = ColorItemPart(color),
        )
    }
}