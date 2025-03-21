package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import at.orchaldir.gm.core.model.util.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.appearance.HAIR_STYLES
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "hats-with-hair.svg",
        CHARACTER_CONFIG,
        addNames(HAIR_STYLES),
        addNames(HatStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), listOf(Hat(style, SaddleBrown)))
    }
}

private fun createAppearance(distance: Distance, style: HairStyle) =
    HeadOnly(
        Head(ears = NormalEars(), eyes = TwoEyes(), hair = NormalHair(style, Yellow)),
        distance,
    )