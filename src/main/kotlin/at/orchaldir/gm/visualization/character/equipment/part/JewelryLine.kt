package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Chain
import at.orchaldir.gm.core.model.item.equipment.style.JewelryLine
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentLine
import at.orchaldir.gm.core.model.item.equipment.style.Wire
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Line2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.calculatePointsOnLine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions

fun visualizeJewelryLine(
    state: State,
    renderer: LayerRenderer,
    jewelryLine: JewelryLine,
    line: Line2d,
    thickness: Distance,
) {
    when (jewelryLine) {
        is Chain -> {
            val wireOptions = LineOptions(jewelryLine.color.toRender(), thickness)
            renderer.renderLine(line, wireOptions)
        }

        is OrnamentLine -> {
            val radius = thickness / 2.0f

            calculatePointsOnLine(line, thickness).forEach { center ->
                visualizeOrnament(state, renderer, jewelryLine.ornament, center, radius)
            }
        }

        is Wire -> {
            val wireOptions = LineOptions(jewelryLine.color.toRender(), thickness)
            renderer.renderLine(line, wireOptions)
        }
    }
}


fun visualizeWire(
    renderer: LayerRenderer,
    top: Point2d,
    bottom: Point2d,
    thickness: Distance,
    color: Color,
) {
    val wireOptions = LineOptions(color.toRender(), thickness)

    renderer.renderLine(listOf(top, bottom), wireOptions)
}