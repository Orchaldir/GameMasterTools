package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Rectangle
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.NormalFoot
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength.Hip
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "suit-style.svg",
        CHARACTER_CONFIG,
        listOf(
            createSingle(Size.Small),
            createSingle(Size.Medium),
            createSingle(Size.Large),
            createDouble(Size.Small),
            createDouble(Size.Medium),
            createDouble(Size.Large),
        ),
        addNames(NecklineStyle.DeepV)
    ) { distance, neckline, opening ->
        Pair(createAppearance(distance), createSuite(neckline, opening))
    }
}

private fun createButton(size: Size) = ButtonColumn(Button(size), 4u)

private fun createSingle(size: Size) = Pair("SingleBreasted - $size", SingleBreasted(ButtonColumn(Button(size), 4u)))

private fun createDouble(space: Size) =
    Pair("DoubleBreasted - $space", DoubleBreasted(ButtonColumn(Button(), 4u), space))

private fun createSuite(
    neckline: NecklineStyle,
    opening: OpeningStyle,
) = from(
    listOf(
        Pants(),
        Shirt(),
        SuitJacket(
            neckline, SleeveStyle.Long, opening, main = FillItemPart(Blue)
        )
    )
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )