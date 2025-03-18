package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "glasses.svg",
        CHARACTER_CONFIG,
        addNames(FrameType.entries),
        addNames(LensShape.entries),
    ) { distance, lensShape, frameType ->
        Pair(createAppearance(distance), listOf(Glasses(lensShape, frameType)))
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