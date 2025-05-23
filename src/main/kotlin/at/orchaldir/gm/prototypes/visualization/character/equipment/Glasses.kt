package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "glasses.svg",
        CHARACTER_CONFIG,
        addNames(FrameType.entries),
        addNames(LensShape.entries),
    ) { distance, lensShape, frameType ->
        val glasses = Glasses(lensShape, frameType)

        Pair(createAppearance(distance), from(glasses))
    }
}

private fun createAppearance(distance: Distance) =
    HeadOnly(
        Head(
            NormalEars(),
            TwoEyes(),
            mouth = NormalMouth(),
        ),
        distance,
    )