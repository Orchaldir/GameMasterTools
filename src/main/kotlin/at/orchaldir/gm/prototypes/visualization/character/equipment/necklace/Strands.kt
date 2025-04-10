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
import at.orchaldir.gm.core.model.util.Color.White
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val pearl = SimpleOrnament(OrnamentShape.Circle, White)

    renderCharacterTable(
        "strands.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(StrandType.entries),
    ) { distance, type, size ->
        val strand = when (type) {
            StrandType.Chain -> Chain(size)
            StrandType.Ornament -> OrnamentChain(pearl)
            StrandType.Wire -> Wire(size)
        }
        val style = StrandNecklace(1, strand)
        Pair(createAppearance(distance), from(Necklace(style)))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(Hourglass),
        Head(),
        height,
    )