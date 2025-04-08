package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.style.BuckleShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBuckle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "belts-buckles.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(BuckleShape.entries)
    ) { distance, shape, size ->
        Pair(createAppearance(distance), createBelt(shape, size))
    }
}

private fun createBelt(
    shape: BuckleShape,
    size: Size,
) = EquipmentMap(
    mapOf(
        BodySlot.BeltSlot to Belt(SimpleBuckle(shape, size)),
        BodySlot.BottomSlot to Pants(),
        BodySlot.TopSlot to Shirt(),
    )
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(BodyShape.Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )