package at.orchaldir.gm.prototypes.visualization.character.equipment.shield

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBoss
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.FixedColor
import at.orchaldir.gm.core.model.util.render.HorizontalStripesLookup
import at.orchaldir.gm.core.model.util.render.VerticalStripesLookup
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.shape.CircularShape.*
import at.orchaldir.gm.utils.math.shape.RectangularShape.*
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val shapes = listOf(
        UsingCircularShape(Circle),
        UsingCircularShape(Square),
        UsingCircularShape(RoundedSquare),
        UsingCircularShape(Hexagon),
        UsingRectangularShape(Rectangle),
        UsingRectangularShape(RoundedRectangle),
        UsingRectangularShape(Ellipse),
        UsingRectangularShape(ReverseTeardrop),
    )

    renderCharacterTableWithoutColorScheme(
        State(),
        "shield-shapes.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(shapes),
    ) { distance, shape, size ->
        val necklace = Shield(
            shape,
            size,
            NoShieldBoss,
            FillLookupItemPart(
                fill = HorizontalStripesLookup(
                    FixedColor(Color.Blue),
                    FixedColor(Color.Yellow),
                )
            )
        )
        Pair(createAppearance(distance), from(necklace))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )