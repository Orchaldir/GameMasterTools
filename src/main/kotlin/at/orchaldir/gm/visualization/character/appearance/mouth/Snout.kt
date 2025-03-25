package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.mouth.Snout
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeSnout(state: CharacterRenderState, snout: Snout) {
    if (!state.renderFront) {
        return
    }

    when (snout.shape) {
        SnoutShape.Cat -> visualizeCat(state, snout)
        SnoutShape.Cow -> visualizeCow(state, snout)
        SnoutShape.Dog -> visualizeDog(state, snout)
        SnoutShape.Pig -> visualizePig(state, snout)
        SnoutShape.Reptile -> visualizeReptile(state, snout)
    }
}

private fun visualizeCat(state: CharacterRenderState, snout: Snout) {
    val radius = state.aabb.convertHeight(Factor(0.08f))
    val thickness = state.aabb.convertHeight(Factor(0.04f))
    val options = NoBorder(snout.color.toRender())
    val lineOptions = LineOptions(snout.color.toRender(), thickness)
    val center = state.aabb.getPoint(CENTER, Factor(0.6f))
    val lineCenter = state.aabb.getPoint(CENTER, Factor(0.8f))
    val (left, right) = state.aabb.getMirroredPoints(Factor(0.2f), Factor(0.85f))

    state.renderer.getLayer().apply {
        renderCircleArc(center, radius, HALF_CIRCLE, HALF_CIRCLE, options)
        renderLine(listOf(center, lineCenter), lineOptions)
        renderLine(listOf(left, lineCenter), lineOptions)
        renderLine(listOf(right, lineCenter), lineOptions)
    }
}

private fun visualizeCow(state: CharacterRenderState, snout: Snout) =
    visualizeRoundedSnoutWithCircleNostrils(
        state,
        snout,
        Factor(0.6f),
        Factor(1.1f),
        Factor(1.1f),
        Factor(0.7f),
        Factor(0.8f),
        Factor(0.1f),
    )

private fun visualizeDog(state: CharacterRenderState, snout: Snout) {
    val options = state.config.getLineOptions(snout.color)
    val lineThickness = Factor(0.04f)
    val lineHalf = lineThickness / 2.0f
    val mouthY = Factor(0.85f)
    val mouthWidth = Factor(0.3f)
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, Factor(0.2f), Factor(0.6f))
        .addMirroredPoints(state.aabb, Factor(0.2f), Factor(0.7f))
        .addMirroredPoints(state.aabb, lineThickness, Factor(0.75f))
        .addMirroredPoints(state.aabb, lineThickness, mouthY - lineHalf, true)
        .addMirroredPoints(state.aabb, mouthWidth, mouthY - lineHalf, true)
        .addMirroredPoints(state.aabb, mouthWidth, mouthY + lineHalf, true)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(polygon, options)
}

private fun visualizePig(state: CharacterRenderState, snout: Snout) =
    visualizeRoundedSnoutWithCircleNostrils(
        state,
        snout,
        Factor(0.6f),
        Factor(0.9f),
        Factor(0.3f),
        Factor(0.15f),
        Factor(0.75f),
        Factor(0.05f),
    )

private fun visualizeReptile(state: CharacterRenderState, snout: Snout) {
    val options = NoBorder(Color.Black.toRender())
    val noseWidth = state.aabb.convertHeight(Factor(0.05f))
    val noseHeight = noseWidth / 2.0f
    val (left, right) = state.aabb.getMirroredPoints(Factor(0.2f), Factor(0.6f))
    val orientation = Orientation.fromDegree(45.0f)
    val renderer = state.renderer.getLayer()

    visualizeMaleMouth(state, Size.Medium)

    renderer.renderEllipse(left, orientation, noseWidth, noseHeight, options)
    renderer.renderEllipse(right, -orientation, noseWidth, noseHeight, options)
}

private fun visualizeRoundedSnoutWithCircleNostrils(
    state: CharacterRenderState,
    snout: Snout,
    upperY: Factor,
    lowerY: Factor,
    width: Factor,
    distanceBetweenNostrils: Factor,
    nostrilY: Factor,
    nostrilRadius: Factor,
) {
    val options = state.config.getLineOptions(snout.color)
    val nostrilOptions = NoBorder(Color.Black.toRender())
    val polygon = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, upperY)
        .addMirroredPoints(state.aabb, width, upperY)
        .addMirroredPoints(state.aabb, width, (upperY + lowerY) / 2.0f)
        .addMirroredPoints(state.aabb, width, lowerY)
        .addLeftPoint(state.aabb, CENTER, lowerY)
        .build()
    val (left, right) = state.aabb.getMirroredPoints(distanceBetweenNostrils, nostrilY)
    val nostrilRadius = state.aabb.convertHeight(nostrilRadius)

    state.renderer.getLayer().apply {
        renderRoundedPolygon(polygon, options)
        renderCircle(left, nostrilRadius, nostrilOptions)
        renderCircle(right, nostrilRadius, nostrilOptions)
    }
}
