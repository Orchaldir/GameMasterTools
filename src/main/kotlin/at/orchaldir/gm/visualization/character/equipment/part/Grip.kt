package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.BoundGrip
import at.orchaldir.gm.core.model.item.equipment.style.Grip
import at.orchaldir.gm.core.model.item.equipment.style.GripShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleGrip
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.utils.visualizeBoundRows

data class GripConfig(
    val thinnerWidth: Factor,
)

fun visualizeGrip(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: GripConfig,
    grip: Grip,
    aabb: AABB,
) = when (grip) {
    is BoundGrip -> visualizeBoundGrip(state, renderer, grip, aabb)
    is SimpleGrip -> visualizeSimpleGrip(state, renderer, config, grip, aabb)
}

private fun visualizeBoundGrip(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    grip: BoundGrip,
    aabb: AABB,
) {
    val color = grip.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    visualizeBoundRows(renderer, options, aabb, grip.rows)
}

private fun visualizeSimpleGrip(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: GripConfig,
    grip: SimpleGrip,
    aabb: AABB,
) {
    val fill = grip.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = createSimpleGripPolygon(config, grip, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSimpleGripPolygon(
    config: GripConfig,
    grip: SimpleGrip,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (grip.shape) {
        GripShape.Oval -> builder
            .addLeftPoint(aabb, CENTER, START)
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, END)
            .addLeftPoint(aabb, CENTER, END)

        GripShape.Straight -> builder
            .addMirroredPoints(aabb, FULL, START, true)
            .addMirroredPoints(aabb, FULL, END, true)

        GripShape.Waisted -> builder
            .addMirroredPoints(aabb, config.thinnerWidth, START, true)
            .addMirroredPoints(aabb, FULL, CENTER, true)
            .addMirroredPoints(aabb, config.thinnerWidth, END, true)
    }

    return builder.build()
}