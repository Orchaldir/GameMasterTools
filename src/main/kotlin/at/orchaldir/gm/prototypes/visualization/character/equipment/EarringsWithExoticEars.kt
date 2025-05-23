package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.StudEarring
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val sizes = mutableListOf<Pair<Size, Size>>()

    Size.entries.forEach { ear ->
        Size.entries.forEach { earring ->
            sizes.add(Pair(ear, earring))
        }
    }

    renderCharacterTableWithoutColorScheme(
        State(),
        "earrings-with-exotic-ears.svg",
        CHARACTER_CONFIG,
        addNames(sizes),
        addNames(EarShape.entries),
    ) { distance, earShape, (earSize, earringSize) ->
        val earring = Earring(StudEarring(size = earringSize))
        val entry = EquipmentEntry<EquipmentData>(earring, setOf(setOf(BodySlot.LeftEar), setOf(BodySlot.RightEar)))

        Pair(
            createAppearance(distance, earShape, earSize),
            EquipmentMap(entry),
        )
    }
}

private fun createAppearance(height: Distance, earShape: EarShape, size: Size) =
    HeadOnly(Head(ears = NormalEars(earShape, size), eyes = TwoEyes()), height, ExoticSkin())
