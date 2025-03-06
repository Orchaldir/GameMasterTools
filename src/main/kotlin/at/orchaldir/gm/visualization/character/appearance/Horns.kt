package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeHorns(state: CharacterRenderState, horns: Horns) = when (horns) {
    NoHorns -> doNothing()
    is TwoHorns -> {
        visualizeHorn(state, horns.horn, Side.Left)
        visualizeHorn(state, horns.horn, Side.Right)
    }

    is DifferentHorns -> {
        visualizeHorn(state, horns.left, Side.Left)
        visualizeHorn(state, horns.right, Side.Right)
    }
}

private fun visualizeHorn(state: CharacterRenderState, horn: Horn, side: Side) = when (horn) {
    is CurvedHorn -> visualizeCurvedHorn(state, side, horn)
}

private fun visualizeCurvedHorn(
    state: CharacterRenderState,
    side: Side,
    horn: CurvedHorn,
) {
    val options = FillAndBorder(horn.color.toRender(), state.config.line)
    val layer = if (state.renderFront) {
        WING_LAYER
    } else {
        -WING_LAYER
    }

    var polygon = createLeftCurvedHorn(state, horn)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.aabb.mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftCurvedHorn(state: CharacterRenderState, horn: CurvedHorn): Polygon2d {
    val builder = Polygon2dBuilder()

    when (horn.position) {
        HornPosition.Brow -> createLeftCurvedHornAtSide(state, horn, builder)
        HornPosition.Side -> createLeftCurvedHornAtSide(state, horn, builder)
        HornPosition.Top -> createLeftCurvedHornAtSide(state, horn, builder)
    }


    val polygon = builder.build()
    return polygon
}

private fun createLeftCurvedHornAtSide(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {

    val y = Factor(0.8f)
    val halfWidth = horn.width / 2.0f

    builder.addPoint(state.aabb, FULL, y - halfWidth)
    builder.addPoint(state.aabb, FULL, y + halfWidth)
    builder.addPoint(state.aabb, FULL + horn.length, y)
}