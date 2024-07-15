package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.renderRoundedPolygon

private val HEAD_WIDTH = Factor(1.0f)

data class HairConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
)

fun visualizeHair(state: RenderState, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is NormalHair -> visualizeNormalHair(state, head.hair)
    }
}

private fun visualizeNormalHair(state: RenderState, hair: NormalHair) {
    val config = state.config
    val options = FillAndBorder(hair.color.toRender(), config.line)

    if (!state.renderFront) {
        state.renderer.renderRectangle(state.aabb, options)
        return
    }

    when (hair.style) {
        is BuzzCut ->
            visualizeRectangleHair(state, options, HEAD_WIDTH, Factor(0.0f))

        is FlatTop ->
            visualizeRectangleHair(state, options, HEAD_WIDTH, config.head.hair.flatTopY, Factor(1.1f))

        is MiddlePart -> visualizeMiddlePart(state, options, CENTER)

        ShavedHair -> doNothing()

        is SidePart -> when (hair.style.side) {
            Side.Left -> visualizeMiddlePart(
                state,
                options,
                END - config.head.hair.sidePartX
            )

            Side.Right -> visualizeMiddlePart(
                state,
                options,
                config.head.hair.sidePartX
            )
        }

        is Spiked -> visualizeSpikedHair(state, options)
    }
}

private fun visualizeMiddlePart(
    state: RenderState,
    options: FillAndBorder,
    x: Factor,
) {
    val aabb = state.aabb
    val config = state.config
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(HEAD_WIDTH, config.head.hairlineY)
    val (topLeft, topRight) = aabb.getMirroredPoints(HEAD_WIDTH, config.head.hair.spikedY)
    val bottomCenter = aabb.getPoint(x, config.head.hairlineY)
    val topCenter = aabb.getPoint(x, Factor(0.0f))

    renderRoundedPolygon(
        state.renderer, options, listOf(
            topLeft,
            topLeft,
            bottomLeft,
            bottomLeft,
            bottomCenter,
            topCenter,
            bottomCenter,
            bottomRight,
            bottomRight,
            topRight,
            topRight,
        )
    )
}

private fun visualizeRectangleHair(
    state: RenderState,
    options: FillAndBorder,
    width: Factor,
    topY: Factor,
    topWidth: Factor = FULL,
) {
    val (bottomLeft, bottomRight) = state.aabb.getMirroredPoints(width, state.config.head.hairlineY)
    val (topLeft, topRight) = state.aabb.getMirroredPoints(width * topWidth, topY)

    renderPolygon(state.renderer, options, listOf(bottomLeft, bottomRight, topRight, topLeft))
}

private fun visualizeSpikedHair(
    state: RenderState,
    options: FillAndBorder,
) {
    val (bottomLeft, bottomRight) = state.aabb.getMirroredPoints(HEAD_WIDTH, state.config.head.hairlineY)
    val (topLeft, topRight) = state.aabb.getMirroredPoints(HEAD_WIDTH, state.config.head.hair.spikedY)
    val points = mutableListOf<Point2d>()
    val spikes = 8
    val topPoints = splitLine(topLeft, topRight, spikes)
    val down = Point2d(0.0f, state.aabb.convertHeight(state.config.head.hair.spikedHeight).value)

    for (i in 0..spikes) {
        val spike = topPoints[i]
        val nextSpike = topPoints[i + 1]
        val middle = (spike + nextSpike) / 2.0f
        val bottomBetweenSpikes = middle + down

        points.add(spike)
        points.add(bottomBetweenSpikes)
    }

    points.add(topRight)
    points.add(bottomRight)
    points.add(bottomLeft)

    renderPolygon(state.renderer, options, points)
}
