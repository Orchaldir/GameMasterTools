package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsKeyMap
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color.White
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "skirts.svg",
        CHARACTER_CONFIG,
        addNames(SkirtStyle.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, style ->
        Pair(
            createAppearance(distance, shape),
            fromSlotAsKeyMap(
                mapOf(
                    BodySlot.Top to Shirt(main = FillLookupItemPart(White)),
                    BodySlot.Bottom to Skirt(style),
                )
            )
        )
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )