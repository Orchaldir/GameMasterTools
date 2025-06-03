package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeSwordHilt(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SwordHilt,
    aabb: AABB,
) = when (hilt) {
    is SimpleHilt -> visualizeSimpleHilt(state, renderer, config, hilt, aabb)
}

private fun visualizeSimpleHilt(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SimpleHilt,
    aabb: AABB,
): Point2d {
    visualizeGrip(state, renderer, config, hilt.grip, aabb)

    return visualizeGuard(state, renderer, config, hilt.guard, aabb)
}

private fun visualizeGrip(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    grip: SwordGrip,
    aabb: AABB,
) {
    val fill = grip.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = createGripPolygon(config, grip, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createGripPolygon(
    config: SwordConfig,
    grip: SwordGrip,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (grip.shape) {
        SwordGripShape.Oval -> builder
            .addLeftPoint(aabb, CENTER, START)
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, END)
            .addLeftPoint(aabb, CENTER, END)

        SwordGripShape.Straight -> builder
            .addMirroredPoints(aabb, FULL, START, true)
            .addMirroredPoints(aabb, FULL, END, true)

        SwordGripShape.Waisted -> builder
            .addMirroredPoints(aabb, config.gripThinnerWidth, START, true)
            .addMirroredPoints(aabb, FULL, CENTER, true)
            .addMirroredPoints(aabb, config.gripThinnerWidth, END, true)
    }

    return builder.build()
}

private fun visualizeGuard(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    guard: SwordGuard,
    gripAabb: AABB,
) = when (guard) {
    NoSwordGuard -> gripAabb.getPoint(CENTER, START)
    is SimpleSwordGuard -> visualizeSimpleGuard(state, renderer, config, guard, gripAabb)
}

private fun visualizeSimpleGuard(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    guard: SimpleSwordGuard,
    gripAabb: AABB,
): Point2d {
    val guardSize = Size2d(
        gripAabb.size.width * guard.width,
        state.aabb.convertHeight(guard.height),
    )
    val bottom = gripAabb.getPoint(CENTER, START)
    val guardAabb = AABB.fromBottom(bottom, guardSize)
    val fill = guard.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    renderer.renderRectangle(guardAabb, options)

    return guardAabb.getPoint(CENTER, START)
}
