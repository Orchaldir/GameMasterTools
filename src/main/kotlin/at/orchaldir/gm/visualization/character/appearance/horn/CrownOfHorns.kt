package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.horn.CrownOfHorns
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeCrownOfHorns(state: CharacterRenderState, crown: CrownOfHorns) {
    val options = FillAndBorder(crown.color.toRender(), state.config.line)
    val length = state.aabb.convertHeight(crown.length)
    val half = state.aabb.convertHeight(crown.width) / 2.0f
    val pair = state.aabb.getMirroredPoints(FULL, state.config.head.hornConfig.y)
    val layer = state.config.head.hornConfig.getLayer(state.renderFront)

    renderLineOfHorns(state, options, length, half, pair, crown.front, layer)
    renderLineOfHorns(state, options, length, half, pair, crown.back, -layer)
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
    val frontSplitter = LineSplitter.fromStartAndEnd(pair, horns)

    frontSplitter.getCenters().forEach { position ->
        val polygon = createHorn(length, half, position)
        state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
    }
}

private fun createHorn(
    length: Distance,
    half: Distance,
    position: Point2d,
): Polygon2d {
    val builder = Polygon2dBuilder()

    val bottomLeft = position.minusWidth(half).addHeight(half)
    val bottomRight = position.plus(half)
    val centerLeft = position.minus(half)
    val centerRight = position.addWidth(half).minusHeight(half)
    val top = position.minusHeight(half + length)

    builder.addPoints(bottomLeft, bottomRight)
    builder.addPoints(centerLeft, centerRight)
    builder.addLeftPoint(top, true)

    return builder.build()
}