package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Ears
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.NoEars
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.style.DangleEarring
import at.orchaldir.gm.core.model.item.equipment.style.StudEarring
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament

data class EarringConfig(
    val studSize: SizeConfig<Factor>,
) {

    fun calculateEarRadius(aabb: AABB, head: HeadConfig, ears: Ears) = when (ears) {
        NoEars -> error("Earrings require ears!")
        is NormalEars -> head.ears.getRoundRadius(aabb, ears.size)
    }

    fun calculatePosition(aabb: AABB, head: HeadConfig, earRadius: Distance): Pair<Point2d, Point2d> {
        val (left, right) = aabb.getMirroredPoints(FULL, head.earY)
        val offset = earRadius * 0.3f
        val leftOffset = Point2d(-offset.toMeters(), offset.toMeters())
        val rightOffset = Point2d(offset.toMeters(), offset.toMeters())

        return Pair(left + leftOffset, right + rightOffset)
    }

    fun calculateStudSize(earRadius: Distance, size: Size) = earRadius * studSize.convert(size)
}

fun visualizeEarrings(
    state: CharacterRenderState,
    head: Head,
    earring: Earring,
) {
    val earRadius = state.config.equipment.earring.calculateEarRadius(state.aabb, state.config.head, head.ears)
    val (left, right) = state.config.equipment.earring.calculatePosition(state.aabb, state.config.head, earRadius)

    visualizeEarring(state, earring, left, earRadius)
    visualizeEarring(state, earring, right, earRadius)
}

private fun visualizeEarring(
    state: CharacterRenderState,
    earring: Earring,
    position: Point2d,
    earRadius: Distance,
) {
    when (earring.style) {
        is DangleEarring -> visualizeDangleEarring(state, earring.style, position, earRadius)
        is StudEarring -> visualizeStudEarring(state, earring.style, position, earRadius)
    }
}

private fun visualizeDangleEarring(
    state: CharacterRenderState,
    dangle: DangleEarring,
    position: Point2d,
    earRadius: Distance,
) {
    var lastStep = earRadius
    var lastPosition = position
    val wireLength = state.config.equipment.earring.calculateStudSize(earRadius, Size.Small)
    val wireOptions = LineOptions(dangle.wireColor.toRender(), wireLength / 3.0f)

    dangle.sizes.forEach { size ->
        val radius = state.config.equipment.earring.calculateStudSize(earRadius, size)
        val top = lastPosition.addHeight(lastStep)
        val center = top.addHeight(radius)

        state.renderer.getLayer().renderLine(listOf(lastPosition, top), wireOptions)

        visualizeOrnament(state, dangle.ornament, center, radius)

        lastStep = wireLength
        lastPosition = center.addHeight(radius)
    }
}

private fun visualizeStudEarring(
    state: CharacterRenderState,
    stud: StudEarring,
    position: Point2d,
    earRadius: Distance,
) {
    val radius = state.config.equipment.earring.calculateStudSize(earRadius, stud.size)

    visualizeOrnament(state, stud.ornament, position, radius)
}
