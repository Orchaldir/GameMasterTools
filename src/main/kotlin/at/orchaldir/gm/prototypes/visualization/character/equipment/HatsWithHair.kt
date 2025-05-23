package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.render.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "hats-with-hair.svg",
        CHARACTER_CONFIG,
        addNames(ShortHairStyle.entries),
        addNames(HatStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Hat(style, SaddleBrown)))
    }
}

private fun createAppearance(distance: Distance, style: ShortHairStyle) =
    HeadOnly(
        Head(ears = NormalEars(), eyes = TwoEyes(), hair = NormalHair(ShortHairCut(style), Yellow)),
        distance,
    )