package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.wing.NoWings
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "tails.svg",
        CHARACTER_CONFIG,
        addNames(listOf(Size.Medium)),
        addNames(SimpleTailShape.entries),
        true,
    ) { distance, tail, size ->
        Pair(createAppearance(distance, tail, size), emptyList())
    }
}

private fun createAppearance(distance: Distance, tailShape: SimpleTailShape, size: Size) =
    HumanoidBody(
        Body(),
        Head(eyes = TwoEyes()),
        distance,
        SimpleTail(tailShape, size, Color.Blue),
        NoWings,
    )