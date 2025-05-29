package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.LamellarArmour
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.shape.RectangularShape.*
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val shapes = listOf(
        UsingRectangularShape(Heater),
        UsingRectangularShape(RoundedHeater),
        UsingRectangularShape(Rectangle),
        UsingRectangularShape(RoundedRectangle),
        UsingRectangularShape(Ellipse),
    )

    renderCharacterTableWithoutColorScheme(
        State(),
        "lamellar-armour-scales.svg",
        CHARACTER_CONFIG,
        addNames(listOf(5, 6, 7, 8)),
        addNames(shapes),
    ) { distance, shape, columns ->
        val armour = LamellarArmour(
            shape = shape,
            columns = columns,
        )
        Pair(createAppearance(distance), from(armour))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )