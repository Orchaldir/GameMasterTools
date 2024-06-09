package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.splitLine
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.renderPolygon

private val HEAD_WIDTH = Factor(1.0f)

data class HairConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
)

fun visualizeHair(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is ShortHair -> visualizeShortHair(renderer, config, aabb, head.hair)
    }
}

fun visualizeShortHair(renderer: Renderer, config: RenderConfig, aabb: AABB, shortHair: ShortHair) {
    val options = FillAndBorder(shortHair.color.toRender(), config.line)

    when (shortHair.style) {
        ShortHairStyle.Afro -> {
            val center = aabb.getPoint(Factor(0.5f), Factor(0.0f))
            val radius = aabb.convertHeight(config.head.hair.afroDiameter * 0.5f)
            renderer.renderCircle(center, radius, options, BEHIND_LAYER)
        }
        ShortHairStyle.BuzzCut ->
            visualizeRectangleHair(renderer, config, options, aabb, HEAD_WIDTH, Factor(0.0f))

        ShortHairStyle.Curly -> doNothing()
        ShortHairStyle.FlatTop ->
            visualizeRectangleHair(renderer, config, options, aabb, HEAD_WIDTH, config.head.hair.flatTopY)
        ShortHairStyle.MiddlePart -> doNothing()
        ShortHairStyle.LeftSidePart -> doNothing()
        ShortHairStyle.RightSidePart -> doNothing()
        ShortHairStyle.Spiked -> visualizeSpikedHair(renderer, config, options, aabb)
    }
}

private fun visualizeRectangleHair(
    renderer: Renderer,
    config: RenderConfig,
    options: FillAndBorder,
    aabb: AABB,
    width: Factor,
    topY: Factor,
) {
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(width, config.head.hairlineY)
    val (topLeft, topRight) = aabb.getMirroredPoints(width, topY)

    renderPolygon(renderer, options, listOf(bottomLeft, bottomRight, topRight, topLeft))
}

private fun visualizeSpikedHair(
    renderer: Renderer,
    config: RenderConfig,
    options: FillAndBorder,
    aabb: AABB,
) {
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(HEAD_WIDTH, config.head.hairlineY)
    val (topLeft, topRight) = aabb.getMirroredPoints(HEAD_WIDTH, config.head.hair.spikedY)
    val points = mutableListOf<Point2d>()
    val spikes = 8
    val topPoints = splitLine(topLeft, topRight, spikes)
    val down = Point2d(0.0f, aabb.convertHeight(config.head.hair.spikedHeight).value)

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

    renderPolygon(renderer, options, points)
}
