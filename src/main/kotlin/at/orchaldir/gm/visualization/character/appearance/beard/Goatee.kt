package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.mouth.Mouth
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun getChinPuff(state: CharacterRenderState, head: Head): Polygon2d {
    val width = state.config.head.getGoateeWidth(head.mouth)
    return getSharpMouthAndBottom(state, head.mouth, width, state.config.head.getGoateeBottomY())
}

fun getGoatee(state: CharacterRenderState, head: Head): Polygon2d {
    val width = state.config.head.getGoateeWidth(head.mouth)
    val (topLeft, topRight) = state.headAABB().getMirroredPoints(width, fromPercentage(95))
    val bottom = state.headAABB().getPoint(CENTER, state.config.head.getGoateeBottomY())

    return Polygon2d(listOf(topLeft, topRight, bottom))
}

fun getLandingStrip(state: CharacterRenderState, head: Head): Polygon2d {
    val width = state.config.head.getGoateeWidth(head.mouth)
    return getSharpMouthAndBottom(state, head.mouth, width, END)
}

fun getSoulPatch(state: CharacterRenderState, head: Head): Polygon2d {
    val topY = state.config.head.mouth.getBottomY(head.mouth)
    val size = state.config.head.beard.mediumThickness
    return fromTopAndBottom(state.headAABB(), topY, topY + size, size, size).build()
}

fun getVanDyke(state: CharacterRenderState, head: Head): Polygon2d {
    val size = state.config.head.beard.mediumThickness
    return getSharpMouthAndBottom(state, head.mouth, size, state.config.head.getGoateeBottomY())
}

private fun getSharpMouthAndBottom(
    state: CharacterRenderState,
    mouth: Mouth,
    width: Factor,
    bottomY: Factor,
): Polygon2d {
    val builder = fromMouthAndBottom(state, mouth, bottomY, width, width)

    builder.createSharpCorners(0)

    return builder.build()
}

private fun fromMouthAndBottom(
    state: CharacterRenderState,
    mouth: Mouth,
    bottomY: Factor,
    topWidth: Factor,
    bottomWidth: Factor,
) = fromTopAndBottom(state.headAABB(), state.config.head.mouth.getBottomY(mouth), bottomY, topWidth, bottomWidth)

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