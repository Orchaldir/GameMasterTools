package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.utils.visualizeBoundRows
import kotlin.math.ceil

fun visualizePolearmFixation(
    state: CharacterRenderState,
    shaftAabb: AABB,
    fixation: PolearmFixation,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER, 1)

    when (fixation) {
        NoPolearmFixation -> doNothing()
        is BoundPolearmHead -> visualizeBoundFixation(state, renderer, shaftAabb, fixation)
        is Langets -> visualizeLangets(state, renderer, shaftAabb, fixation)
        is SocketedPolearmHead -> visualizeSocketedFixation(state, renderer, shaftAabb, fixation)
    }
}

fun visualizeBoundFixation(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    fixation: BoundPolearmHead,
) {
    val config = state.config.equipment.polearm
    val height = shaftAabb.convertHeight(fixation.length)
    val rowHeight = shaftAabb.convertHeight(config.boundRowThickness)
    val rows = ceil(height.toMeters() / rowHeight.toMeters()).toInt()
    val color = fixation.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val boundAabb = shaftAabb.growWidthByPadding(config.boundPadding)

    visualizeBoundRows(renderer, options, boundAabb, rowHeight, rows)
}

fun visualizeLangets(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    fixation: Langets,
) {
    state.config.equipment.polearm
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(shaftAabb, HALF, START)
        .addMirroredPoints(shaftAabb, HALF, fixation.length)
        .build()
    val color = fixation.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    renderer.renderPolygon(polygon, options)
}

fun visualizeSocketedFixation(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    fixation: SocketedPolearmHead,
) {
    val config = state.config.equipment.polearm
    val padding = config.socketedPadding
    val doublePadding = padding * 2
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(shaftAabb, FULL + doublePadding, START)
        .addMirroredPoints(shaftAabb, FULL + doublePadding, fixation.length)
        .build()
    val color = fixation.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    renderer.renderPolygon(polygon, options)
}

