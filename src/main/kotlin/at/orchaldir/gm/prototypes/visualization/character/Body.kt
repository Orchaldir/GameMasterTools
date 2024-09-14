package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "body.svg",
        RENDER_CONFIG,
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