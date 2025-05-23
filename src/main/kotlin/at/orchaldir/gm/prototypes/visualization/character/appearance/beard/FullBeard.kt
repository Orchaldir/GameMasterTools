package at.orchaldir.gm.prototypes.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.beard.FullBeard
import at.orchaldir.gm.core.model.character.appearance.beard.FullBeardStyle
import at.orchaldir.gm.core.model.character.appearance.beard.NormalBeard
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "beard-full.svg",
        CHARACTER_CONFIG,
        addNames(FullBeardStyle.entries),
        addNames(HairLength.entries),
    ) { distance, length, style ->
        Pair(createAppearance(distance, style, length), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, style: FullBeardStyle, length: HairLength) =
    HumanoidBody(
        Body(),
        Head(
            NormalEars(),
            TwoEyes(),
            NoHair,
            NoHorns,
            NormalMouth(NormalBeard(FullBeard(style, length), SaddleBrown))
        ),
        height,
    )