package at.orchaldir.gm.prototypes.visualization.character.equipment.necklace

import at.orchaldir.gm.core.model.character.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Hourglass
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val strand = Wire()
    val strands: List<Pair<String, StrandNecklace>> = listOf(
        Pair("1", StrandNecklace(1, strand)),
        Pair("2", StrandNecklace(2, strand)),
        Pair("3", StrandNecklace(3, strand)),
    )

    renderCharacterTable(
        "necklaces-strands.svg",
        CHARACTER_CONFIG,
        strands,
        addNames(NecklaceLength.entries),
    ) { distance, length, style ->
        val necklace = Necklace(style, length)
        Pair(createAppearance(distance), from(necklace))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(Hourglass),
        Head(),
        height,
    )