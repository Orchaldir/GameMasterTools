package at.orchaldir.gm.prototypes.visualization.character.equipment.eyepatch

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

val FIXATIONS: List<Pair<String, EyePatchFixation>> = listOf(
    Pair("None", NoFixation),
    Pair("OneBand + Small", OneBand(Small)),
    Pair("OneBand + Medium", OneBand(Medium)),
    Pair("OneBand + Large", OneBand(Large, SaddleBrown)),
    Pair("DiagonalBand + Small", DiagonalBand(Small)),
    Pair("DiagonalBand + Medium", DiagonalBand(Medium)),
    Pair("DiagonalBand + Large", DiagonalBand(Large, SaddleBrown)),
    Pair("TwoBands", TwoBands()),
)

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "eyepatch-simple.svg",
        CHARACTER_CONFIG,
        addNames(VALID_LENSES),
        FIXATIONS,
        true,
    ) { distance, fixation, shape ->
        val eyePatch = EyePatch(SimpleEyePatch(shape, Color.Red), fixation)
        val entry = EquipmentMapEntry<EquipmentData>(eyePatch, BodySlot.LeftEye)

        Pair(createAppearance(distance), EquipmentMap(entry))
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