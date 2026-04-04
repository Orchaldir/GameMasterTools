package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val styles = listOf(
        Boot(),
        KneeHighBoot(),
        Pumps(),
        Sandal(),
        Shoe(),
        SimpleShoe(),
        Slipper(),
    )
    val namedStyles = styles.map { style -> Pair(style.getType().name, style) }

    renderCharacterTableWithoutColorScheme(
        State(),
        "footwear.svg",
        CHARACTER_CONFIG,
        addNames(listOf(BodyShape.Rectangle)),
        namedStyles,
        true,
    ) { distance, style, shape ->
        Pair(createAppearance(distance, shape), from(Footwear(style)))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )