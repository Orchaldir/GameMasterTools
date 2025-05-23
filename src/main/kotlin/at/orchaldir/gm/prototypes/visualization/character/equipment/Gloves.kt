package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Gloves
import at.orchaldir.gm.core.model.item.equipment.style.GloveStyle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color.Blue
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "gloves.svg",
        CHARACTER_CONFIG,
        addNames(listOf(BodyShape.Rectangle)),
        addNames(GloveStyle.entries),
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Gloves(style, Blue)))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )