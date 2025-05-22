package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Chain
import at.orchaldir.gm.core.model.item.equipment.style.JewelryLine
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentLine
import at.orchaldir.gm.core.model.item.equipment.style.Wire
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.utils.math.Line2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.calculatePointsOnLine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeJewelryLine(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    jewelryLine: JewelryLine,
    line: Line2d,
    thickness: Distance,
) {
    when (jewelryLine) {
        is Chain -> {
            val color = jewelryLine.main.getColor(state.state, state.colors)
            val wireOptions = LineOptions(color.toRender(), thickness)
            renderer.renderLine(line, wireOptions)
        }

        is OrnamentLine -> {
            val radius = thickness / 2.0f

            calculatePointsOnLine(line, thickness).forEach { center ->
                visualizeOrnament(state, renderer, jewelryLine.ornament, center, radius)
            }
        }

        is Wire -> {
            val color = jewelryLine.main.getColor(state.state, state.colors)
            val wireOptions = LineOptions(color.toRender(), thickness)
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