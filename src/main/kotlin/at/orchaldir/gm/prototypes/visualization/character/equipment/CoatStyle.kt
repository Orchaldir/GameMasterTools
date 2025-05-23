package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Rectangle
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.NormalFoot
import at.orchaldir.gm.core.model.item.FillLookupItemPart
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength.Hip
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color.Blue
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "coat-style.svg",
        CHARACTER_CONFIG,
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
        Pair(createAppearance(distance), from(createCoat(neckline, opening)))
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
    Hip, neckline, SleeveStyle.Long, opening, main = FillLookupItemPart(Blue)
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )