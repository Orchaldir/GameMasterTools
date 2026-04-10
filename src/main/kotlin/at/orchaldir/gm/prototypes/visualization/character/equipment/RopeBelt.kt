package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.BuckleAndStrap
import at.orchaldir.gm.core.model.item.equipment.style.RopeBelt
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBuckle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "belts-rope.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(Size.entries)
    ) { distance, thickness, length ->
        Pair(createAppearance(distance), createBelt(thickness, length))
    }
}

private fun createBelt(
    thickness: Size,
    length: Size,
): EquipmentMap<EquipmentData> {
    val style = RopeBelt(MadeFromCord(Color.Green), thickness, length)

    return EquipmentMap.fromSlotAsKeyMap(
        mapOf(
            BodySlot.Belt to Belt(style),
            BodySlot.Bottom to Pants(),
            BodySlot.Top to Shirt(),
        )
    )
}

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(BodyShape.Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )