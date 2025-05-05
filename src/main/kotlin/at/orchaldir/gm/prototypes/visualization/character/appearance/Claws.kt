package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Muscular
import at.orchaldir.gm.core.model.character.appearance.ClawedFoot
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Size.Medium
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "claws.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(listOf(1, 2, 3, 4, 5))
    ) { distance, shape, width ->
        Pair(createAppearance(distance, shape, width), EquipmentMap())
    }
}

private fun createAppearance(distance: Distance, count: Int, size: Size) =
    HumanoidBody(
        Body(Muscular, ClawedFoot(count, size), Medium),
        Head(),
        distance,
    )