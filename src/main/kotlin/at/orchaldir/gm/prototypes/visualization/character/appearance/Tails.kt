package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.wing.NoWings
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    renderCharacterTable(
        "tails.svg",
        CHARACTER_CONFIG,
        addNames(SimpleTailShape.entries),
        addNames(BodyShape.entries)
    ) { distance, body, tail ->
        Pair(createAppearance(distance, body, tail), emptyList())
    }
}

private fun createAppearance(distance: Distance, bodyShape: BodyShape, tailShape: SimpleTailShape) =
    HumanoidBody(
        Body(bodyShape),
        Head(),
        distance,
        SimpleTail(tailShape, Solid(Color.Blue)),
        NoWings,
    )