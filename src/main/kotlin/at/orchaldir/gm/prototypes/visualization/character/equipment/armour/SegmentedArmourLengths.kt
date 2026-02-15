package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.ContinueLegArmour
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "segmented-armour-lengths.svg",
        CHARACTER_CONFIG,
        addNames(BodyShape.entries),
        addNames(OuterwearLength.entries),
    ) { distance, length, bodyShape ->
        val style = SegmentedArmour(breastplateRows = 2)
        val armour = BodyArmour(style, ContinueLegArmour(length))

        Pair(createAppearance(distance, bodyShape), from(armour))
    }
}

private fun createAppearance(height: Distance, bodyShape: BodyShape) =
    HumanoidBody(
        Body(bodyShape),
        Head(),
        height,
    )