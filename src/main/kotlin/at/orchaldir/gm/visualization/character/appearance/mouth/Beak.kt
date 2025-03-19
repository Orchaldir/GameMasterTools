package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeBeak(state: CharacterRenderState, beak: Beak) = when (beak.shape) {
    BeakShape.Crow -> visualizeCrow(state, beak)
    BeakShape.Duck -> visualizeDuckBeak(state, beak)
    BeakShape.Hawk -> visualizeHawk(state, beak)
    BeakShape.Parrot -> visualizeParrot(state, beak)
}

private fun visualizeCrow(state: CharacterRenderState, beak: Beak) = visualizeSharpBeak(
    state,
    beak,
    Factor(0.3f),
    Factor(0.18f),
    Factor(0.15f),
    Factor(0.45f),
    false,
)

private fun visualizeHawk(state: CharacterRenderState, beak: Beak) = visualizeSharpBeak(
    state,
    beak,
    Factor(0.4f),
    Factor(0.15f),
    Factor(0.25f),
    Factor(0.35f),
    true,
)

private fun visualizeParrot(state: CharacterRenderState, beak: Beak) {
    val width = Factor(0.4f)
    val upperHeight = Factor(0.25f)
    val peakHeight = Factor(0.35f)
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val upperPolygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width * 0.5f, y - upperHeight)
        .addMirroredPoints(state.aabb, width, y)
        .addLeftPoint(state.aabb, CENTER, y + peakHeight, true)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}

private fun visualizeSharpBeak(
    state: CharacterRenderState,
    beak: Beak,
    upperHeight: Factor,
    lowerHeight: Factor,
    peakHeight: Factor,
    width: Factor,
    isSharp: Boolean,
) {
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val lowerPolygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y + lowerHeight)
        .build()
    val upperPolygon = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, y - upperHeight)
        .addMirroredPoints(state.aabb, width, y, true)
        .addMirroredPoints(state.aabb, width / 3.0f, y)
        .addLeftPoint(state.aabb, CENTER, y + peakHeight, isSharp)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(lowerPolygon, options)
    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}

private fun visualizeDuckBeak(state: CharacterRenderState, beak: Beak) {
    val width = Factor(0.4f)
    val upperHeight = Factor(0.25f)
    val peakHeight = Factor(0.35f)
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val upperPolygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, Factor(0.1f), y - Factor(0.2f))
        .addMirroredPoints(state.aabb, Factor(0.2f), y - Factor(0.1f))
        .addMirroredPoints(state.aabb, Factor(0.4f), y - Factor(0.0f))
        .addMirroredPoints(state.aabb, Factor(0.4f), y + Factor(0.1f))
        .addLeftPoint(state.aabb, CENTER, y + Factor(0.05f))
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}