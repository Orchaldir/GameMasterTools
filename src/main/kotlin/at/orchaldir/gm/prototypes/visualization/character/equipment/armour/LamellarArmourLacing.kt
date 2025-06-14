package at.orchaldir.gm.prototypes.visualization.character.equipment.armour

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.*
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
    val lacing = listOf(
        Pair("None", NoLacing),
        Pair("Diagonal", DiagonalLacing()),
        Pair("4 Sides", FourSidesLacing()),
        Pair("Stripe", LacingAndStripe()),
    )

    renderCharacterTableWithoutColorScheme(
        State(),
        "lamellar-armour-lacing.svg",
        CHARACTER_CONFIG,
        lacing,
        addNames(shapes),
    ) { distance, shape, lacing ->
        val style = LamellarArmour(
            shape = shape,
            lacing = lacing,
        )
        val armour = BodyArmour(style)
        Pair(createAppearance(distance), from(armour))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )