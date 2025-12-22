package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun getChinPuff(state: CharacterRenderState<Head>): Polygon2d {
    val width = state.config.head.getGoateeWidth(state)
    return getSharpMouthAndBottom(state, width, state.config.head.getGoateeBottomY())
}

fun getGoatee(state: CharacterRenderState<Head>): Polygon2d {
    val width = state.config.head.getGoateeWidth(state)
    val (topLeft, topRight) = state.headAABB().getMirroredPoints(width, fromPercentage(95))
    val bottom = state.headAABB().getPoint(CENTER, state.config.head.getGoateeBottomY())

    return Polygon2d(listOf(topLeft, topRight, bottom))
}

fun getLandingStrip(state: CharacterRenderState<Head>): Polygon2d {
    val width = state.config.head.getGoateeWidth(state)
    return getSharpMouthAndBottom(state, width, END)
}

fun getSoulPatch(state: CharacterRenderState<Head>): Polygon2d {
    val topY = state.config.head.mouth.getBottomY(state)
    val size = state.config.head.beard.mediumThickness
    return fromTopAndBottom(state.headAABB(), topY, topY + size, size, size).build()
}

fun getVanDyke(state: CharacterRenderState<Head>): Polygon2d {
    val size = state.config.head.beard.mediumThickness
    return getSharpMouthAndBottom(state, size, state.config.head.getGoateeBottomY())
}

private fun getSharpMouthAndBottom(
    state: CharacterRenderState<Head>,
    width: Factor,
    bottomY: Factor,
): Polygon2d {
    val builder = fromMouthAndBottom(state, bottomY, width, width)

    builder.createSharpCorners(0)

    return builder.build()
}

private fun fromMouthAndBottom(
    state: CharacterRenderState<Head>,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
) = fromTopAndBottom(state.headAABB(), state.config.head.mouth.getBottomY(state), bottomY, topWidth, bottomWidth)

private fun fromTopAndBottom(
    aabb: AABB,
    topY: Factor,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    builder.addMirroredPoints(aabb, topWidth, topY)
    builder.addMirroredPoints(aabb, bottomWidth, bottomY)

    return builder
}