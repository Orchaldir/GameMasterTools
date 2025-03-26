package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.Socks
import at.orchaldir.gm.core.model.item.equipment.style.SocksStyle
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.HorizontalStripes
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "socks.svg",
        CHARACTER_CONFIG,
        addNames(listOf(BodyShape.Rectangle)),
        addNames(SocksStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), listOf(Socks(style, HorizontalStripes(Color.White, Color.Blue, 1u))))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )