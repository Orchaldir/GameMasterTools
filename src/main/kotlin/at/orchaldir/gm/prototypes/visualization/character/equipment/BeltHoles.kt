package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsKeyMap
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "belts-holes.svg",
        CHARACTER_CONFIG,
        addNames(listOf(null, Color.Silver, Color.Gold)),
        addNames(
            listOf(
                Pair(BeltHolesType.OneRow, Size.Small),
                Pair(BeltHolesType.OneRow, Size.Medium),
                Pair(BeltHolesType.OneRow, Size.Large),
                Pair(BeltHolesType.TwoRows, Size.Medium),
                Pair(BeltHolesType.ThreeRows, Size.Medium),
            )
        ),
    ) { distance, (type, size), color ->
        Pair(createAppearance(distance), createBelt(type, size, color))
    }

}

private fun createBelt(
    type: BeltHolesType,
    size: Size,
    color: Color?,
): EquipmentElementMap {
    val belt = Belt(
        SimpleBuckle(
            BuckleShape.Rectangle,
        ),
        holes = when (type) {
            BeltHolesType.NoBeltHoles -> NoBeltHoles
            BeltHolesType.OneRow -> OneRowOfBeltHoles(size, color)
            BeltHolesType.TwoRows -> TwoRowsOfBeltHoles(color)
            BeltHolesType.ThreeRows -> ThreeRowsOfBeltHoles(color)
        }
    )

    return fromSlotAsKeyMap(
        mapOf(
            BodySlot.Belt to belt,
            BodySlot.Bottom to Pants(),
            BodySlot.Top to Shirt(),
        ).mapValues { Pair(it.value, UndefinedColors) }
    )
}

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(BodyShape.Rectangle, NormalFoot, Size.Medium),
        Head(),
        distance,
    )