package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Spike
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun <T> visualizeSpike(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    spike: Spike,
    start: Point2d,
    orientation: Orientation,
    parentSize: Distance,
) {
    val color = state.getColor(spike.part)
    val options = state.config.getLineOptions(color)

    val length = parentSize * spike.length
    val halfWidth = length * spike.width * HALF
    val end = start.createPolar(length, orientation)
    val left = start.createPolar(halfWidth, orientation - QUARTER_CIRCLE)
    val right = start.createPolar(halfWidth, orientation + QUARTER_CIRCLE)
    val polygon = Polygon2d(listOf(left, end, right))

    renderer.renderPolygon(polygon, options)
}

fun <T> visualizeTopDownSpike(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    spike: Spike,
    position: Point2d,
    parentSize: Distance,
) {
    val color = state.getColor(spike.part)
    val options = state.config.getLineOptions(color)

    val length = parentSize * spike.length
    val halfWidth = length * spike.width * HALF

    renderer.renderCircle(position, halfWidth, options)
}
