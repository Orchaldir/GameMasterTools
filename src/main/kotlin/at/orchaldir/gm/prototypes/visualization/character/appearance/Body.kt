package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderCharacterTable(
        "body.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, width ->
        Pair(createAppearance(distance, shape, width), emptyList())
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape, width: Size) =
    HumanoidBody(
        Body(shape, width),
        Head(),
        distance,
    )