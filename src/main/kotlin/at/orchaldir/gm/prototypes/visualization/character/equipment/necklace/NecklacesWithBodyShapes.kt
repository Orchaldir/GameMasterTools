package at.orchaldir.gm.prototypes.visualization.character.equipment.necklace

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.NecklaceLength
import at.orchaldir.gm.core.model.item.equipment.style.StrandNecklace
import at.orchaldir.gm.core.model.item.equipment.style.Wire
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "necklaces-body.svg",
        CHARACTER_CONFIG,
        addNames(BodyShape.entries),
        addNames(NecklaceLength.entries),
    ) { distance, length, bodyShape ->
        val necklace = Necklace(StrandNecklace(1, Wire()), length)
        Pair(createAppearance(distance, bodyShape), from(necklace))
    }
}

private fun createAppearance(height: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape),
        Head(),
        height,
    )