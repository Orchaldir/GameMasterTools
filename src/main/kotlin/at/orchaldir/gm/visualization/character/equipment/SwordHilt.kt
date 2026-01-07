package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.part.visualizeGrip
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament
import at.orchaldir.gm.visualization.utils.visualizeBoundRows

fun visualizeSwordHilt(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SwordHilt,
    aabb: AABB,
) = when (hilt) {
    is SimpleSwordHilt -> visualizeSimpleHilt(state, renderer, config, hilt, aabb)
}

private fun visualizeSimpleHilt(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SimpleSwordHilt,
    aabb: AABB,
): Point2d {
    visualizeGrip(state, renderer, config.grip, hilt.grip, aabb)
    visualizePommel(state, renderer, config, hilt.pommel, aabb)

    return visualizeGuard(state, renderer, config, hilt.guard, aabb)
}

private fun visualizeGuard(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    guard: SwordGuard,
    gripAabb: AABB,
) = when (guard) {
    NoSwordGuard -> gripAabb.getPoint(CENTER, START)
    is SimpleSwordGuard -> visualizeSimpleGuard(state, renderer, config, guard, gripAabb)
}

private fun visualizeSimpleGuard(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    guard: SimpleSwordGuard,
    gripAabb: AABB,
): Point2d {
    val guardSize = Size2d(
        gripAabb.size.width * guard.width,
        state.fullAABB.convertHeight(guard.height),
    )
    val bottom = gripAabb.getPoint(CENTER, START)
    val guardAabb = AABB.fromBottom(bottom, guardSize)
    val fill = guard.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    renderer.renderRectangle(guardAabb, options)

    return guardAabb.getPoint(CENTER, START)
}

private fun visualizePommel(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    pommel: Pommel,
    gripAabb: AABB,
) = when (pommel) {
    NoPommel -> doNothing()
    is PommelWithOrnament -> visualizePommelWithOrnament(state, renderer, config, pommel, gripAabb)
}

private fun visualizePommelWithOrnament(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    config: SwordConfig,
    pommel: PommelWithOrnament,
    gripAabb: AABB,
) {
    val width = gripAabb.size.width * config.pommelSizes.convert(pommel.size)
    val top = gripAabb.getPoint(CENTER, END)
    val aabb = AABB.fromTop(top, Size2d.square(width))

    visualizeOrnament(state, renderer, pommel.ornament, aabb, true)
}
