package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament
import at.orchaldir.gm.visualization.utils.visualizeBoundRows

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
    visualizePommel(state, renderer, config, hilt.pommel, aabb)

    return visualizeGuard(state, renderer, config, hilt.guard, aabb)
}

private fun visualizeGrip(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    grip: SwordGrip,
    aabb: AABB,
) = when (grip) {
    is BoundSwordGrip -> visualizeBoundGrip(state, renderer, config, grip, aabb)
    is SimpleSwordGrip -> visualizeSimpleGrip(state, renderer, config, grip, aabb)
}

private fun visualizeBoundGrip(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    grip: BoundSwordGrip,
    aabb: AABB,
) {
    val color = grip.part.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)

    visualizeBoundRows(renderer, options, aabb, grip.rows)
}

private fun visualizeSimpleGrip(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    grip: SimpleSwordGrip,
    aabb: AABB,
) {
    val fill = grip.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = createSimpleGripPolygon(config, grip, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSimpleGripPolygon(
    config: SwordConfig,
    grip: SimpleSwordGrip,
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

private fun visualizePommel(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    pommel: Pommel,
    gripAabb: AABB,
) = when (pommel) {
    NoPommel -> doNothing()
    is PommelWithOrnament -> visualizePommelWithOrnament(state, renderer, config, pommel, gripAabb)
}

private fun visualizePommelWithOrnament(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    pommel: PommelWithOrnament,
    gripAabb: AABB,
) {
    val width = gripAabb.size.width * config.pommelSizes.convert(pommel.size)
    val top = gripAabb.getPoint(CENTER, END)
    val aabb = AABB.fromTop(top, Size2d.square(width))

    visualizeOrnament(state, renderer, pommel.ornament, aabb)
}
