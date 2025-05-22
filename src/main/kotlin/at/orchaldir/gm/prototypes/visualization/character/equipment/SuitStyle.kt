package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Rectangle
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.NormalFoot
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.Gray
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
        addNames(PocketStyle.entries),
        listOf(
            createSingle(Size.Medium),
            createDouble(Size.Small),
        )
    ) { distance, opening, pocket ->
        Pair(createAppearance(distance), createSuite(pocket, opening))
    }
}

private fun createSingle(size: Size) = Pair("SingleBreasted - $size", SingleBreasted(ButtonColumn(Button(size), 4u)))

private fun createDouble(space: Size) =
    Pair("DoubleBreasted - $space", DoubleBreasted(ButtonColumn(Button(), 4u), space))

private fun createSuite(
    pocket: PocketStyle,
    opening: OpeningStyle,
) = from(
    listOf(
        Footwear(),
        Pants(main = FillItemPart(Color.Silver)),
        Shirt(),
        SuitJacket(
            NecklineStyle.DeepV, SleeveStyle.Long, opening, pocket, FillItemPart(Gray)
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