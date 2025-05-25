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
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.render.Color.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.shape.CircularShape.Circle
import at.orchaldir.gm.utils.math.shape.RectangularShape.Cross
import at.orchaldir.gm.utils.math.shape.RectangularShape.Teardrop
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val pearl = SimpleOrnament(Circle, White)
    val dangleNecklace = DangleNecklace(
        DangleEarring(
            pearl,
            pearl,
            listOf(Small, Medium, Large)
        ),
        OrnamentLine(pearl, Medium),
    )
    val dropNecklace = DropNecklace(
        DropEarring(
            Factor.fromPercentage(20),
            Factor.fromPercentage(40),
            Factor.fromPercentage(100),
            SimpleOrnament(Circle, Silver),
            OrnamentWithBorder(Teardrop, Blue, Silver),
            ColorSchemeItemPart(Silver),
        ),
        Wire(Small, Silver),
    )
    val crossNecklace = PendantNecklace(
        SimpleOrnament(Cross, Silver),
        Wire(Small, Black),
    )
    val styles: MutableList<Pair<String, NecklaceStyle>> = mutableListOf(
        Pair("Pearl", StrandNecklace(1, OrnamentLine(pearl))),
        Pair("Dangle", dangleNecklace),
        Pair("Drop", dropNecklace),
        Pair("Cross", crossNecklace),
    )
    Size.entries.forEach { size ->
        styles.add(Pair("$size Pendant", PendantNecklace(OrnamentWithBorder(), Wire(size), size)))
    }

    renderCharacterTableWithoutColorScheme(
        State(),
        "necklaces-styles.svg",
        CHARACTER_CONFIG,
        styles,
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