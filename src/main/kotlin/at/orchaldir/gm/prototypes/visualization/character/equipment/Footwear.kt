package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.core.model.util.render.Color.Gray
import at.orchaldir.gm.core.model.util.render.Color.SaddleBrown
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "footwear.svg",
        CHARACTER_CONFIG,
        addNames(listOf(BodyShape.Rectangle)),
        addNames(FootwearStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Footwear(style, SaddleBrown, Gray)))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )