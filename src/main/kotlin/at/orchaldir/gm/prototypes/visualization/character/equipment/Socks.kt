package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
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
        State(),
        "socks.svg",
        CHARACTER_CONFIG,
        addNames(listOf(BodyShape.Rectangle)),
        addNames(SocksStyle.entries),
        true,
    ) { distance, style, shape ->
        val itemPart = FillItemPart(fill = HorizontalStripes(Color.White, Color.Blue, 1u))
        val socks = Socks(style, itemPart)

        Pair(createAppearance(distance, shape), from(socks))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )