package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle.MiddlePart
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.render.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "hats-with-ears.svg",
        CHARACTER_CONFIG,
        addNames(EarShape.entries),
        addNames(HatStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Hat(style, SaddleBrown)))
    }
}

private fun createAppearance(distance: Distance, shape: EarShape) =
    HeadOnly(
        Head(ears = NormalEars(shape), eyes = TwoEyes(), hair = NormalHair(ShortHairCut(MiddlePart), Yellow)),
        distance,
    )