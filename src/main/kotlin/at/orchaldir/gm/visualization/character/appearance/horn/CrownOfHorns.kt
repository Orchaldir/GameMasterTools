package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.horn.CrownOfHorns
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeCrownOfHorns(
    state: CharacterRenderState,
    crown: CrownOfHorns,
    skin: Skin,
    hair: Hair,
) {
    val options = state.config.getFeatureOptions(state.state, crown.color, hair, skin)
    val aabb = state.headAABB()
    val length = aabb.convertHeight(crown.length)
    val half = aabb.convertHeight(crown.width) / 2.0f
    val pair = aabb.getMirroredPoints(FULL, state.config.head.hornConfig.y)
    val layer = state.config.head.hornConfig.getLayer(state.renderFront)

    renderLineOfHorns(state, options, length, half, pair, crown.front, layer)
    renderLineOfHorns(state, options, length, half, pair, crown.back, -layer)

    if (crown.hasSideHorns) {
        renderSideHorn(state, options, length, half, pair.second, layer, Side.Left)
        renderSideHorn(state, options, length, half, pair.second, layer, Side.Right)
    }
}

private fun renderLineOfHorns(
    state: CharacterRenderState,
    options: RenderOptions,
    length: Distance,
    half: Distance,
    pair: Pair<Point2d, Point2d>,
    horns: Int,
    layer: Int,
) {
    val frontSplitter = SegmentSplitter.fromStartAndEnd(pair, horns)

    frontSplitter.getCenters().forEach { position ->
        val polygon = createHornOfLine(length, half, position)
        state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
    }
}

private fun createHornOfLine(
    length: Distance,
    half: Distance,
    position: Point2d,
): Polygon2d {
    val builder = Polygon2dBuilder()
    val top = position.minusHeight(half + length)

    builder.addSquare(position, half)
    builder.addLeftPoint(top, true)

    return builder.build()
}

private fun renderSideHorn(
    state: CharacterRenderState,
    options: RenderOptions,
    length: Distance,
    half: Distance,
    position: Point2d,
    layer: Int,
    side: Side,
) {
    var polygon = createLeftSideHorn(length, half, position)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.headAABB().mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftSideHorn(
    length: Distance,
    half: Distance,
    position: Point2d,
): Polygon2d {
    val builder = Polygon2dBuilder()
    val width = half * 2

    val bottomInner = position.addHeight(half)
    val bottomOuter = bottomInner.addWidth(width)
    val centerInner = position.minusHeight(half)
    val centerOuter = centerInner.addWidth(width)
    val top = position.addWidth(half).minusHeight(length + half)

    builder.addLeftPoint(bottomInner, true)
    builder.addRightPoint(bottomOuter)
    builder.addLeftPoint(centerInner, true)
    builder.addRightPoint(centerOuter)
    builder.addLeftPoint(top, true)

    return builder.build()
}