package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.style.StudEarring
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val sizes = mutableListOf<Pair<Size, Size>>()

    Size.entries.forEach { ear ->
        Size.entries.forEach { earring ->
            sizes.add(Pair(ear, earring))
        }
    }

    renderCharacterTable(
        State(),
        "earrings-with-exotic-ears.svg",
        CHARACTER_CONFIG,
        addNames(sizes),
        addNames(EarShape.entries),
    ) { distance, earShape, (earSize, earringSize) ->
        val earring = Earring(StudEarring(size = earringSize))

        Pair(
            createAppearance(distance, earShape, earSize),
            EquipmentMap(earring to setOf(setOf(BodySlot.LeftEar), setOf(BodySlot.RightEar)))
        )
    }
}

private fun createAppearance(height: Distance, earShape: EarShape, size: Size) =
    HeadOnly(Head(ears = NormalEars(earShape, size), eyes = TwoEyes()), height, ExoticSkin())
