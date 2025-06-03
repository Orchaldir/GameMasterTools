package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.OneHandedSword
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val hilts = listOf(
        Pair("Simple", SimpleHilt(grip = SwordGrip(part = FillLookupItemPart(Color.Black)))),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "swords.svg",
        CHARACTER_CONFIG,
        hilts,
        addNames(BladeShape.entries),
    ) { distance, shape, hilt ->
        val sword = OneHandedSword(
            SimpleBlade(shape),
            hilt,
        )
        Pair(createAppearance(distance), from(sword))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )