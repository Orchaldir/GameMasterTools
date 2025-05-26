package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormatType
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.Segment
import at.orchaldir.gm.core.model.util.part.SegmentShape.RoundedCylinder
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val rollLength = fromMillimeters(200)
    val rollDiameter = fromMillimeters(50)
    val handle0 = Segments(Segment(fromPercentage(20), fromPercentage(30), Color.SaddleBrown))
    val handle1 = Segments(
        listOf(
            Segment(fromPercentage(20), fromPercentage(30), Color.Gold),
            Segment(fromPercentage(10), fromPercentage(80), Color.Gold, RoundedCylinder),
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