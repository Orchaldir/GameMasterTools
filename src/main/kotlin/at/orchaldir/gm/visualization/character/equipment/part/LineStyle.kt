package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Chain
import at.orchaldir.gm.core.model.item.equipment.style.LineStyle
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentLine
import at.orchaldir.gm.core.model.item.equipment.style.Wire
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Line2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.calculatePointsOnLine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun <T> visualizeLineStyle(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    style: LineStyle,
    line: Line2d,
    thickness: Distance,
) {
    when (style) {
        is Chain -> {
            val color = style.main.getColor(state.state, state.colors)
            val wireOptions = LineOptions(color.toRender(), thickness)
            renderer.renderLine(line, wireOptions)
        }

        is OrnamentLine -> {
            val radius = thickness / 2.0f

            calculatePointsOnLine(line, thickness).forEach { center ->
                visualizeOrnament(state, renderer, style.ornament, center, radius)
            }
        }

        is Wire -> {
            val color = style.main.getColor(state.state, state.colors)
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