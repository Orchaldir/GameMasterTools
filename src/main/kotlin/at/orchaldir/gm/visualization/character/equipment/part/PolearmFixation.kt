package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.PolearmConfig

fun visualizePolearmFixation(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    fixation: PolearmFixation,
) = when (fixation) {
    NoPolearmFixation -> doNothing()
    is BoundPolearmHead -> doNothing()
    is Langets -> doNothing()
    is SocketedPolearmHead -> visualizeSocketedFixation(state, renderer, shaftAabb, fixation)
}

fun visualizeSocketedFixation(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    fixation: SocketedPolearmHead,
) {
    val padding = state.config.equipment.polearm.socketedPadding
    val doublePadding = padding * 2
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(shaftAabb, FULL + doublePadding, -padding)
        .addMirroredPoints(shaftAabb, FULL + doublePadding, START, true)
        .addMirroredPoints(shaftAabb, FULL + doublePadding, fixation.length, true)
        .build()
    val color = fixation.part.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)

    renderer.renderRoundedPolygon(polygon, options)
}

