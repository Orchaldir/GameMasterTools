package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Rectangle
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.item.style.OuterwearLength.Hip
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.character.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "coat-style.svg",
        RENDER_CONFIG,
        listOf(
            Pair("None", NoOpening),
            createSingle(Size.Small),
            createSingle(Size.Medium),
            createSingle(Size.Large),
            createDouble(Size.Small),
            createDouble(Size.Medium),
            createDouble(Size.Large),
            Pair("Zipper", Zipper()),
        ),
        addNames(NECKLINES_WITH_SLEEVES)
    ) { distance, neckline, opening ->
        Pair(createAppearance(distance), listOf(createCoat(neckline, opening)))
    }
}

private fun createButton(size: Size) = ButtonColumn(Button(size), 4u)

private fun createSingle(size: Size) = Pair("SingleBreasted - $size", SingleBreasted(ButtonColumn(Button(size), 4u)))

private fun createDouble(space: Size) =
    Pair("DoubleBreasted - $space", DoubleBreasted(ButtonColumn(Button(), 4u), space))

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