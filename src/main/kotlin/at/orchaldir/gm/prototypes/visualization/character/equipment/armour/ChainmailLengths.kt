package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.item.equipment.style.SameLegArmour
import at.orchaldir.gm.core.model.item.equipment.style.DifferentLegArmour
import at.orchaldir.gm.core.model.item.equipment.style.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.LegArmourStyle
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val lengths = listOf(OuterwearLength.Knee, OuterwearLength.Ankle)
    val legStyles = mutableListOf<Pair<String, LegArmourStyle>>()

    OuterwearLength.entries.forEach {
        legStyles.add(Pair(it.name, SameLegArmour(it)))
    }

    lengths.forEach {
        val armour = DifferentLegArmour(ScaleArmour(), it)
        legStyles.add(Pair("Scales to " + it.name, armour))
    }

    lengths.forEach {
        val armour = DifferentLegArmour(SegmentedArmour(), it)
        legStyles.add(Pair("Segmented to " + it.name, armour))
    }

    lengths.forEach {
        val armour = DifferentLegArmour(LamellarArmour(), it)
        legStyles.add(Pair("Lamellar to " + it.name, armour))
    }

    renderCharacterTableWithoutColorScheme(
        State(),
        "chainmail-length.svg",
        CHARACTER_CONFIG,
        addNames(BodyShape.entries),
        legStyles,
    ) { distance, legStyle, bodyShape ->
        val armour = BodyArmour(
            ChainMail(),
            legStyle,
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