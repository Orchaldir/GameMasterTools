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
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Color.Gray
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
        addNames(NecklineStyle.DeepV),
        listOf(
            createSingle(Size.Medium),
            createDouble(Size.Small),
        )
    ) { distance, opening, neckline ->
        Pair(createAppearance(distance), createSuite(neckline, opening))
    }
}

private fun createSingle(size: Size) = Pair("SingleBreasted - $size", SingleBreasted(ButtonColumn(Button(size), 4u)))

private fun createDouble(space: Size) =
    Pair("DoubleBreasted - $space", DoubleBreasted(ButtonColumn(Button(), 4u), space))

private fun createSuite(
    neckline: NecklineStyle,
    opening: OpeningStyle,
) = from(
    listOf(
        Footwear(),
        Pants(main = FillItemPart(Color.Silver)),
        Shirt(),
        SuitJacket(
            neckline, SleeveStyle.Long, opening, PocketStyle.Patch, FillItemPart(Gray)
        ),
        Tie(),
    )
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )