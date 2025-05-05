package at.orchaldir.gm.prototypes.visualization.character.equipment.eyepatch

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape.VerticalSlit
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.EyePatchWithEye
import at.orchaldir.gm.core.model.item.equipment.style.OneBand
import at.orchaldir.gm.core.model.item.equipment.style.VALID_LENSES
import at.orchaldir.gm.core.model.util.Color.Red
import at.orchaldir.gm.core.model.util.Color.Yellow
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "eyepatch-eye.svg",
        CHARACTER_CONFIG,
        addNames(EyeShape.entries),
        addNames(VALID_LENSES),
    ) { distance, shape, eyeShape ->
        val eye = NormalEye(eyeShape, VerticalSlit, Red, Yellow)
        val eyePatch = EyePatch(EyePatchWithEye(eye, shape), OneBand())
        Pair(createAppearance(distance), EquipmentMap(eyePatch, BodySlot.LeftEye))
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