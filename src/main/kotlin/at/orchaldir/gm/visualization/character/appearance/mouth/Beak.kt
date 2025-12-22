package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.mouth.Beak
import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeBeak(state: CharacterRenderState<Head>, beak: Beak) {
    if (!state.renderFront) {
        return
    }

    when (beak.shape) {
        BeakShape.Crow -> visualizeCrow(state, beak)
        BeakShape.Duck -> visualizeDuckBeak(state, beak)
        BeakShape.Hawk -> visualizeHawk(state, beak)
        BeakShape.Parrot -> visualizeParrot(state, beak)
    }
}

private fun visualizeCrow(state: CharacterRenderState<Head>, beak: Beak) = visualizeSharpBeak(
    state,
    beak,
    fromPercentage(30),
    fromPercentage(18),
    fromPercentage(15),
    fromPercentage(45),
    false,
)

private fun visualizeHawk(state: CharacterRenderState<Head>, beak: Beak) = visualizeSharpBeak(
    state,
    beak,
    fromPercentage(40),
    fromPercentage(15),
    fromPercentage(25),
    fromPercentage(35),
    true,
)

private fun visualizeParrot(state: CharacterRenderState<Head>, beak: Beak) {
    val width = fromPercentage(40)
    val upperHeight = fromPercentage(25)
    val peakHeight = fromPercentage(35)
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val aabb = state.headAABB()
    val upperPolygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, width * 0.5f, y - upperHeight)
        .addMirroredPoints(aabb, width, y)
        .addLeftPoint(aabb, CENTER, y + peakHeight, true)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}

private fun visualizeSharpBeak(
    state: CharacterRenderState<Head>,
    beak: Beak,
    upperHeight: Factor,
    lowerHeight: Factor,
    peakHeight: Factor,
    width: Factor,
    isSharp: Boolean,
) {
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val aabb = state.headAABB()
    val lowerPolygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, width, y, true)
        .addMirroredPoints(aabb, width / 3.0f, y + lowerHeight)
        .build()
    val upperPolygon = Polygon2dBuilder()
        .addLeftPoint(aabb, CENTER, y - upperHeight)
        .addMirroredPoints(aabb, width, y, true)
        .addMirroredPoints(aabb, width / 3.0f, y)
        .addLeftPoint(aabb, CENTER, y + peakHeight, isSharp)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(lowerPolygon, options)
    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}

private fun visualizeDuckBeak(state: CharacterRenderState<Head>, beak: Beak) {
    val lowerHeight = fromPermille(125)
    val lowerWidth = fromPercentage(35)
    val upperHeight0 = fromPercentage(20)
    val upperHeight1 = fromPercentage(10)
    val upperWidth = fromPercentage(50)
    val y = state.config.head.mouth.y
    val options = state.config.getLineOptions(beak.color)
    val aabb = state.headAABB()
    val lowerPolygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, lowerWidth, y, true)
        .addMirroredPoints(aabb, lowerWidth, y + lowerHeight)
        .build()
    val upperPolygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, fromPercentage(10), y - upperHeight0)
        .addMirroredPoints(aabb, fromPercentage(20), y - upperHeight1)
        .addMirroredPoints(aabb, upperWidth, y)
        .addMirroredPoints(aabb, upperWidth, y + upperHeight1)
        .addLeftPoint(aabb, CENTER, y + upperHeight1 / 2.0f)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(lowerPolygon, options)
    state.renderer.getLayer()
        .renderRoundedPolygon(upperPolygon, options)
}