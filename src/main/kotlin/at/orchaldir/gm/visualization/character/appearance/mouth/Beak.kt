package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeBeak(state: CharacterRenderState, beak: Beak) = when (beak.shape) {
    BeakShape.Crow -> visualizeCrow(state, beak)
    BeakShape.Duck -> doNothing()
    BeakShape.Hawk -> visualizeHawk(state, beak)
    BeakShape.Owl -> doNothing()
    BeakShape.Parrot -> doNothing()
}

private fun visualizeCrow(state: CharacterRenderState, beak: Beak) {
    val y = state.config.head.mouth.y
    val half = Factor(0.3f)
    val width = Factor(0.45f)
    val options = state.config.getLineOptions(beak.color)
    val lowerPolygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y + half * 0.6f)
        .build()
    val upperPolygon = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, y - half)
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y)
        .addLeftPoint(state.aabb, CENTER, y + half * 0.5f)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(lowerPolygon, options)
    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}

private fun visualizeHawk(state: CharacterRenderState, beak: Beak) {
    val y = state.config.head.mouth.y
    val upperHeight = Factor(0.4f)
    val lowerHeight = Factor(0.3f)
    val width = Factor(0.35f)
    val options = state.config.getLineOptions(beak.color)
    val lowerPolygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y + lowerHeight * 0.5f)
        .build()
    val upperPolygon = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, y - upperHeight)
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y)
        .addLeftPoint(state.aabb, CENTER, y + lowerHeight, true)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(lowerPolygon, options)
    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}