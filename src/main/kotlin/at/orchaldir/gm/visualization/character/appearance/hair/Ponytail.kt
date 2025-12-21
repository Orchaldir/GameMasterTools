package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.Ponytail
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition.BothSides
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition.Right
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailStyle
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailStyle.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAIR_LAYER
import at.orchaldir.gm.visualization.renderRoundedPolygon
import kotlin.math.max

fun visualizePonytail(state: CharacterRenderState<Head>, hair: NormalHair, ponytail: Ponytail) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val layer = state.getLayerIndex(HAIR_LAYER)
    val y = Factor.fromPercentage(20)
    val length = state.config.getHairLength(state, ponytail.length)

    visualizeBackSideOfHead(state, options, HAIR_LAYER + 1)

    val polygon = when (ponytail.position) {
        PonytailPosition.High -> getCenterPonytail(state, ponytail.style, length, y)
        PonytailPosition.Low -> getCenterPonytail(state, ponytail.style, length, FULL - y)
        PonytailPosition.Top -> getCenterPonytail(
            state,
            ponytail.style,
            length,
            START - y - config.head.hair.longPadding
        )

        else -> getLeftPonytail(state, ponytail.style, length, y)
    }

    if (ponytail.position != Right) {
        renderRoundedPolygon(state.renderer, options, polygon, layer)
    }

    if (ponytail.position == Right || ponytail.position == BothSides) {
        val right = state.headAABB().mirrorVertically(polygon)
        renderRoundedPolygon(state.renderer, options, right, layer)
    }
}

private fun getCenterPonytail(
    state: CharacterRenderState<Head>,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
) = when (style) {
    Braid -> getBraid(state, length, CENTER, y, ZERO)
    Straight, Wide -> getCenterStraightPonytail(state, style, length, y)
}

private fun getCenterStraightPonytail(
    state: CharacterRenderState<Head>,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
): Polygon2d {
    val config = state.config.head.hair
    val aabb = state.headAABB()
    val (left, right) = aabb.getMirroredPoints(config.getBottomWidth(style), FULL)

    return Polygon2dBuilder()
        .addLeftPoint(aabb, CENTER, y)
        .addMirroredPoints(aabb, config.ponytailWidth, y)
        .addPoints(left.addHeight(length), right.addHeight(length))
        .build()
}

private fun getLeftPonytail(
    state: CharacterRenderState<Head>,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
) = when (style) {
    Braid -> {
        val braidWidth = state.config.head.hair.braidWidth
        getBraid(state, length, FULL + braidWidth / 2.0f, y, braidWidth)
    }

    Straight, Wide -> getLeftStraightPonytail(state, style, length, y)
}

private fun getBraid(
    state: CharacterRenderState<Head>,
    lengthDistance: Distance,
    startX: Factor,
    startY: Factor,
    offset: Factor,
): Polygon2d {
    val braid = state.config.head.hair.braidWidth
    val half = braid / 2.0f
    val aabb = state.headAABB()
    val length = lengthDistance.toMeters() / aabb.size.height.toMeters() + 1.0 - startY.toNumber()
    val n = max((length / braid.toNumber()).toInt(), 1)
    var x = startX
    var y = startY
    var step = offset
    val builder = Polygon2dBuilder()
        .addHorizontalPoints(aabb, braid, x, y)

    repeat(n) {
        y += half
        x += step / 4.0f
        builder.addLeftPoint(aabb, x - half, y)
        builder.addLeftPoint(aabb, x, y)
        builder.addLeftPoint(aabb, x - half, y)
        y += half
        x += step / 4.0f
        builder.addRightPoint(aabb, x + half, y)
        builder.addRightPoint(aabb, x, y)
        builder.addRightPoint(aabb, x + half, y)
        step /= 2.0f
    }

    y += braid

    return builder
        .addLeftPoint(aabb, x, y)
        .build()
}

private fun getLeftStraightPonytail(
    state: CharacterRenderState<Head>,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
): Polygon2d {
    val config = state.config.head.hair
    val width = config.ponytailWidth
    val bottomWidth = config.getBottomWidth(style)
    val half = width / 2.0f
    val aabb = state.headAABB()
    val left = aabb.getPoint(FULL + half, FULL)
    val right = aabb.getPoint(FULL + half + bottomWidth, FULL)

    return Polygon2dBuilder()
        .addLeftPoint(aabb, FULL, y - half)
        .addRightPoint(aabb, FULL + half + width, y - half)
        .addLeftPoint(aabb, FULL, y + half)
        .addLeftPoint(aabb, FULL + half, y + half)
        .addLeftPoint(left.addHeight(length))
        .addRightPoint(right.addHeight(length))
        .build()
}
