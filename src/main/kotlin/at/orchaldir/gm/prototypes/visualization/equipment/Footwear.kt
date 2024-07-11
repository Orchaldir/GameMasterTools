package at.orchaldir.gm.prototypes.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color.Blue
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.Footwear
import at.orchaldir.gm.core.model.item.FootwearStyle
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "footwear.svg",
        RENDER_CONFIG,
        addNames(FootwearStyle.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, style ->
        Pair(createAppearance(distance, shape), listOf(Footwear(style, Blue, Blue)))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, Size.Medium),
        Head(),
        distance,
    )