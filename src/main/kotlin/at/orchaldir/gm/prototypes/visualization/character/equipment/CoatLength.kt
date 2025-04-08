package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.EquipmentMap.Companion.fromSlotAsKeyMap
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.Button
import at.orchaldir.gm.core.model.item.equipment.style.ButtonColumn
import at.orchaldir.gm.core.model.item.equipment.style.DoubleBreasted
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "coat-length.svg",
        CHARACTER_CONFIG,
        addNames(OuterwearLength.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, length ->
        val coat = Coat(
            length,
            openingStyle = DoubleBreasted(ButtonColumn(Button(Size.Medium, Color.Gold), 5u)),
            fill = Solid(Blue)
        )
        Pair(
            createAppearance(distance, shape),
            EquipmentMap(coat, BodySlot.OuterSlot)
        )
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )