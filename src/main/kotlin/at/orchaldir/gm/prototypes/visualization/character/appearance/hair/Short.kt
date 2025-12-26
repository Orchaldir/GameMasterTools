package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.hair.ExoticHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.EYES
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "hair-short.svg",
        CHARACTER_CONFIG,
        addNames(ShortHairStyle.entries),
        EYES,
        false,
    ) { distance, eyes, style ->
        Pair(createAppearance(distance, eyes, style), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, eyes: Eyes, style: ShortHairStyle) =
    HeadOnly(
        Head(NormalEars(), eyes, ExoticHair(ShortHairCut(style), Color.SaddleBrown), NoHorns, NormalMouth()),
        height,
    )