package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.mouth.Snout
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.unit.HALF_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
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
    val aabb = state.headAABB()
    val radius = aabb.convertHeight(fromPercentage(8))
    val thickness = aabb.convertHeight(fromPercentage(4))
    val options = NoBorder(snout.color.toRender())
    val lineOptions = LineOptions(snout.color.toRender(), thickness)
    val center = aabb.getPoint(CENTER, fromPercentage(60))
    val lineCenter = aabb.getPoint(CENTER, fromPercentage(80))
    val (left, right) = aabb.getMirroredPoints(fromPercentage(20), fromPercentage(85))

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
        fromPercentage(60),
        fromPercentage(110),
        fromPercentage(110),
        fromPercentage(70),
        fromPercentage(80),
        fromPercentage(10),
    )

private fun visualizeDog(state: CharacterRenderState, snout: Snout) {
    val options = state.config.getLineOptions(snout.color)
    val lineThickness = fromPercentage(4)
    val lineHalf = lineThickness / 2.0f
    val mouthY = fromPercentage(85)
    val mouthWidth = fromPercentage(30)
    val aabb = state.headAABB()
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, fromPercentage(20), fromPercentage(60))
        .addMirroredPoints(aabb, fromPercentage(20), fromPercentage(70))
        .addMirroredPoints(aabb, lineThickness, fromPercentage(75))
        .addMirroredPoints(aabb, lineThickness, mouthY - lineHalf, true)
        .addMirroredPoints(aabb, mouthWidth, mouthY - lineHalf, true)
        .addMirroredPoints(aabb, mouthWidth, mouthY + lineHalf, true)
        .build()

    state.renderer.getLayer()
        .renderRoundedPolygon(polygon, options)
}

private fun visualizePig(state: CharacterRenderState, snout: Snout) =
    visualizeRoundedSnoutWithCircleNostrils(
        state,
        snout,
        fromPercentage(60),
        fromPercentage(90),
        fromPercentage(30),
        fromPercentage(15),
        fromPercentage(75),
        fromPercentage(5),
    )

private fun visualizeReptile(state: CharacterRenderState, snout: Snout) {
    val options = NoBorder(Color.Black.toRender())
    val aabb = state.headAABB()
    val noseWidth = aabb.convertHeight(fromPercentage(5))
    val noseHeight = noseWidth / 2.0f
    val (left, right) = aabb.getMirroredPoints(fromPercentage(20), fromPercentage(60))
    val orientation = Orientation.fromDegrees(45)
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
    val aabb = state.headAABB()
    val polygon = Polygon2dBuilder()
        .addLeftPoint(aabb, CENTER, upperY)
        .addMirroredPoints(aabb, width, upperY)
        .addMirroredPoints(aabb, width, (upperY + lowerY) / 2.0f)
        .addMirroredPoints(aabb, width, lowerY)
        .addLeftPoint(aabb, CENTER, lowerY)
        .build()
    val (left, right) = aabb.getMirroredPoints(distanceBetweenNostrils, nostrilY)
    val nostrilRadius = aabb.convertHeight(nostrilRadius)

    state.renderer.getLayer().apply {
        renderRoundedPolygon(polygon, options)
        renderCircle(left, nostrilRadius, nostrilOptions)
        renderCircle(right, nostrilRadius, nostrilOptions)
    }
}
