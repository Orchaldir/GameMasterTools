package at.orchaldir.gm.prototypes.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.MiddlePart
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.style.HatStyle
import at.orchaldir.gm.core.model.util.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "hats-with-ears.svg",
        RENDER_CONFIG,
        addNames(EarShape.entries),
        addNames(HatStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), listOf(Hat(style, SaddleBrown)))
    }
}

private fun createAppearance(distance: Distance, shape: EarShape) =
    HeadOnly(
        Head(ears = NormalEars(shape), eyes = TwoEyes(), hair = NormalHair(MiddlePart, Yellow)),
        distance,
    )