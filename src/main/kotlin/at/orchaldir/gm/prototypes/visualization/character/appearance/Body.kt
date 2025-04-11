package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "body.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, width ->
        Pair(createAppearance(distance, shape, width), EquipmentMap())
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape, width: Size) =
    HumanoidBody(
        Body(shape, NormalFoot, width),
        Head(),
        distance,
    )