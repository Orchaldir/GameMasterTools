package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle.None
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "neckline.svg",
        CHARACTER_CONFIG,
        addNames(NecklineStyle.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, style ->
        Pair(createAppearance(distance, shape), from(Shirt(style, None, Solid(Blue))))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )