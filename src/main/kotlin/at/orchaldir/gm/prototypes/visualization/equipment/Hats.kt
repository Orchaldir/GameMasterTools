package at.orchaldir.gm.prototypes.visualization.equipment

import at.orchaldir.gm.core.model.appearance.Color.*
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.item.Hat
import at.orchaldir.gm.core.model.item.HatStyle
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.HEAR_STYLES
import at.orchaldir.gm.prototypes.visualization.character.addNames
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    renderTable(
        "hats.svg",
        RENDER_CONFIG,
        addNames(HEAR_STYLES),
        addNames(HatStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), listOf(Hat(style, SaddleBrown)))
    }
}

private fun createAppearance(distance: Distance, style: HairStyle) =
    HeadOnly(
        Head(hair = NormalHair(style, Yellow)),
        distance,
    )