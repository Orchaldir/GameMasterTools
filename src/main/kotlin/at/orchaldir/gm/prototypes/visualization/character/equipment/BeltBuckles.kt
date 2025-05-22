package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.BuckleShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBuckle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
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
) = EquipmentMap.fromSlotAsKeyMap(
    mapOf(
        BodySlot.Belt to Belt(SimpleBuckle(shape, size)),
        BodySlot.Bottom to Pants(),
        BodySlot.Top to Shirt(),
    )
)

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(BodyShape.Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )