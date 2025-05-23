package at.orchaldir.gm.prototypes.visualization.character.equipment.eyepatch

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EquipmentEntry
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentAsEyePatch
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "eyepatch-ornament.svg",
        CHARACTER_CONFIG,
        addNames(OrnamentShape.entries),
        FIXATIONS,
    ) { distance, fixation, shape ->
        val eyePatch = EyePatch(OrnamentAsEyePatch(OrnamentWithBorder(shape)), fixation)
        val entry = EquipmentEntry<EquipmentData>(eyePatch, BodySlot.LeftEye)

        Pair(
            createAppearance(distance),
            EquipmentMap(entry)
        )
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