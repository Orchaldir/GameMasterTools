package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.SameLegArmour
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "scale-armour-length.svg",
        CHARACTER_CONFIG,
        addNames(BodyShape.entries),
        addNames(OuterwearLength.entries),
    ) { distance, length, bodyShape ->
        val armour = BodyArmour(
            ScaleArmour(),
            SameLegArmour(length),
            SleeveStyle.Short,
        )

        Pair(createAppearance(distance, bodyShape), from(armour))
    }
}

private fun createAppearance(height: Distance, bodyShape: BodyShape) =
    HumanoidBody(
        Body(bodyShape),
        Head(),
        height,
    )