package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.FemaleMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "hair-long.svg",
        CHARACTER_CONFIG,
        addNames(LongHairStyle.entries),
        addNames(HairLength.entries),
        true,
    ) { distance, length, style ->
        Pair(createAppearance(distance, length, style), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, length: HairLength, style: LongHairStyle) =
    HumanoidBody(
        Body(BodyShape.Hourglass),
        Head(
            NormalEars(),
            TwoEyes(),
            NormalHair(LongHairCut(style, length), Color.SaddleBrown),
            NoHorns,
            FemaleMouth()
        ),
        height,
    )