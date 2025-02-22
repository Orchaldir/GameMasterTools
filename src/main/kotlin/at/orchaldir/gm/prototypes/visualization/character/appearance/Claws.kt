package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Muscular
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderCharacterTable(
        "claws.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(listOf(1, 2, 3, 4, 5, 6))
    ) { distance, shape, width ->
        Pair(createAppearance(distance, shape, width), emptyList())
    }
}

private fun createAppearance(distance: Distance, count: Int, size: Size) =
    HumanoidBody(
        Body(Muscular, ClawedFoot(count, size), size),
        Head(),
        distance,
    )