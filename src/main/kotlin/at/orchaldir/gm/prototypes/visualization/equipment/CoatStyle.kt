package at.orchaldir.gm.prototypes.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color.Blue
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.appearance.Solid
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Rectangle
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.item.style.OuterwearLength.Hip
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val small = createButton(Size.Small)
    val medium = createButton(Size.Medium)
    val large = createButton(Size.Large)

    renderTable(
        "coat-style.svg",
        RENDER_CONFIG,
        listOf(
            Pair("None", NoOpening),
            Pair("SingleBreasted - Small", SingleBreasted(small)),
            Pair("SingleBreasted - Medium", SingleBreasted(medium)),
            Pair("SingleBreasted - Large", SingleBreasted(large)),
            Pair("DoubleBreasted", DoubleBreasted()),
            Pair("Zipper", Zipper()),
        ),
        addNames(NECKLINES_WITH_SLEEVES)
    ) { distance, neckline, opening ->
        Pair(createAppearance(distance), listOf(createCoat(neckline, opening)))
    }
}

private fun createButton(size: Size) = ButtonColumn(Button(size), 4u)

private fun createCoat(
    neckline: NecklineStyle,
    opening: OpeningStyle,
) = Coat(
    Hip, neckline, SleeveStyle.Long, opening, fill = Solid(Blue)
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(Rectangle, Size.Medium),
        Head(),
        distance,
    )