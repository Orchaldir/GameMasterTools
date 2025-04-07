package at.orchaldir.gm.prototypes.visualization.character.equipment.eyepatch

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val fixations: List<Pair<String, EyePatchFixation>> = listOf(
        Pair("None", NoFixation),
        Pair("OneBand", OneBand()),
        Pair("DiagonalBand", DiagonalBand()),
        Pair("TwoBands", TwoBands()),
    )

    renderCharacterTable(
        "eyepatch-simple.svg",
        CHARACTER_CONFIG,
        addNames(VALID_LENSES),
        fixations,
        false,
    ) { distance, fixation, shape ->
        Pair(createAppearance(distance), listOf(EyePatch(SimpleEyePatch(shape), fixation)))
    }
}

private fun createAppearance(height: Distance) =
    HeadOnly(
        Head(
            eyes = TwoEyes(),
            mouth = NormalMouth(),
        ),
        height,
    )