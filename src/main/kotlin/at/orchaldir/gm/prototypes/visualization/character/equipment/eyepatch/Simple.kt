package at.orchaldir.gm.prototypes.visualization.character.equipment.eyepatch

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val fixations: List<Pair<String, EyePatchFixation>> = listOf(
        Pair("None", NoFixation),
        Pair("OneBand + Small", OneBand(Small)),
        Pair("OneBand + Medium", OneBand(Medium)),
        Pair("OneBand + Large", OneBand(Large, SaddleBrown)),
        Pair("DiagonalBand + Small", DiagonalBand(Small)),
        Pair("DiagonalBand + Medium", DiagonalBand(Medium)),
        Pair("DiagonalBand + Large", DiagonalBand(Large, SaddleBrown)),
        Pair("TwoBands", TwoBands()),
    )

    renderCharacterTable(
        "eyepatch-simple.svg",
        CHARACTER_CONFIG,
        addNames(VALID_LENSES),
        fixations,
        true,
    ) { distance, fixation, shape ->
        Pair(createAppearance(distance), listOf(EyePatch(SimpleEyePatch(shape, Color.Red), fixation)))
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