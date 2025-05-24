package at.orchaldir.gm.prototypes.visualization.character.equipment.necklace

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape.Hourglass
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color.White
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.CircularShape.Circle
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val pearl = SimpleOrnament(Circle, White)
    val strand = Wire()
    val strandStyles: MutableList<Pair<String, StrandNecklace>> = mutableListOf(
        Pair("1", StrandNecklace(1, strand)),
    )

    Size.entries.forEach { size ->
        Size.entries.forEach { padding ->
            strandStyles.add(Pair("2 $size $padding", StrandNecklace(2, OrnamentLine(pearl, size), padding)))
        }
    }

    strandStyles.add(Pair("3", StrandNecklace(3, strand)))

    renderCharacterTableWithoutColorScheme(
        State(),
        "necklaces-strands.svg",
        CHARACTER_CONFIG,
        strandStyles,
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