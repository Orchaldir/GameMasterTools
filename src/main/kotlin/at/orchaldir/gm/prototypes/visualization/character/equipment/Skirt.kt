package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.Shirt
import at.orchaldir.gm.core.model.item.Skirt
import at.orchaldir.gm.core.model.item.style.SkirtStyle
import at.orchaldir.gm.core.model.util.Color.White
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.character.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "skirts.svg",
        RENDER_CONFIG,
        addNames(SkirtStyle.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, style ->
        Pair(createAppearance(distance, shape), listOf(Shirt(fill = Solid(White)), Skirt(style)))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, Size.Medium),
        Head(),
        distance,
    )