package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Ears
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.NoEars
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.style.DangleEarring
import at.orchaldir.gm.core.model.item.equipment.style.DropEarring
import at.orchaldir.gm.core.model.item.equipment.style.HoopEarring
import at.orchaldir.gm.core.model.item.equipment.style.StudEarring
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.HALF_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOrnament
import at.orchaldir.gm.visualization.character.equipment.part.visualizeWire

data class EarringConfig(
    val studSize: SizeConfig<Factor>,
    val wireThickness: SizeConfig<Factor>,
) {

    fun calculateEarRadius(aabb: AABB, head: HeadConfig, ears: Ears) = when (ears) {
        NoEars -> error("Earrings require ears!")
        is NormalEars -> head.ears.getRoundRadius(aabb, ears.size)
    }

    fun calculatePosition(aabb: AABB, head: HeadConfig, earRadius: Distance): Pair<Point2d, Point2d> {
        val (left, right) = aabb.getMirroredPoints(FULL, head.earY)
        val offset = earRadius * 0.7f
        val orientation = Orientation.fromDegrees(30)

        return Pair(
            left.createPolar(offset, HALF_CIRCLE - orientation),
            right.createPolar(offset, orientation),
        )
    }

    fun calculateStudSize(earRadius: Distance, size: Size) = earRadius * studSize.convert(size)
    fun calculateWireThickness(earRadius: Distance, size: Size) = earRadius * wireThickness.convert(size)

}

fun visualizeEarring(
    state: CharacterRenderState,
    head: Head,
    earring: Earring,
    set: Set<BodySlot>,
) {
    val config = state.config.equipment.earring
    val earRadius = config.calculateEarRadius(state.aabb, state.config.head, head.ears)
    val (left, right) = config.calculatePosition(state.aabb, state.config.head, earRadius)

    if (set.contains(BodySlot.LeftEar)) {
        visualizeEarring(state, earring, right, earRadius)
    }
    if (set.contains(BodySlot.RightEar)) {
        visualizeEarring(state, earring, left, earRadius)
    }
}

private fun visualizeEarring(
    state: CharacterRenderState,
    earring: Earring,
    position: Point2d,
    earRadius: Distance,
) {
    when (earring.style) {
        is DangleEarring -> visualizeDangleEarring(state, earring.style, position, earRadius)
        is DropEarring -> visualizeDropEarring(state, earring.style, position, earRadius)
        is HoopEarring -> visualizeHoopEarring(state, earring.style, position, earRadius)
        is StudEarring -> visualizeStudEarring(state, earring.style, position, earRadius)
    }
}

fun visualizeDangleEarring(
    state: CharacterRenderState,
    dangle: DangleEarring,
    position: Point2d,
    earRadius: Distance,
) {
    var lastStep = earRadius
    var lastPosition = position
    val config = state.config.equipment.earring
    val topRadius = config.calculateStudSize(earRadius, Size.Small)
    val wireLength = config.calculateStudSize(earRadius, Size.Small)
    val renderer = state.renderer.getLayer(EQUIPMENT_LAYER)
    val color = dangle.wire.getColor(state.state, state.colors)

    visualizeOrnament(state, renderer, dangle.top, position, topRadius)

    dangle.sizes.forEach { size ->
        val radius = config.calculateStudSize(earRadius, size)
        val top = lastPosition.addHeight(lastStep)
        val center = top.addHeight(radius)

        visualizeOrnament(state, renderer, dangle.bottom, center, radius)

        lastStep = wireLength
        lastPosition = center.addHeight(radius)
    }

    visualizeEarringWire(state, earRadius, position, lastPosition, Size.Small, color)
}

private fun visualizeDropEarring(
    state: CharacterRenderState,
    drop: DropEarring,
    start: Point2d,
    earRadius: Distance,
) {
    val maxLength = calculateMaxDrop(state.aabb, start, earRadius)
    visualizeDropEarring(state, drop, start, earRadius, maxLength, EQUIPMENT_LAYER)
}

fun visualizeDropEarring(
    state: CharacterRenderState,
    drop: DropEarring,
    start: Point2d,
    earRadius: Distance,
    maxLength: Distance,
    layer: Int,
) {
    val topRadius = earRadius * drop.topSize
    val bottomRadius = earRadius * drop.bottomSize
    val end = start.addHeight(maxLength * drop.wireLength)
    val renderer = state.renderer.getLayer(layer)
    val color = drop.wire.getColor(state.state, state.colors)

    visualizeEarringWire(state, earRadius, start, end, Size.Small, color)
    visualizeOrnament(state, renderer, drop.top, start, topRadius)
    visualizeOrnament(state, renderer, drop.bottom, end, bottomRadius)
}

private fun visualizeHoopEarring(
    state: CharacterRenderState,
    hoop: HoopEarring,
    position: Point2d,
    earRadius: Distance,
) {
    val maxLength = calculateMaxDrop(state.aabb, position, earRadius)
    val end = position.addHeight(maxLength * hoop.length)
    val color = hoop.wire.getColor(state.state, state.colors)

    visualizeEarringWire(state, earRadius, position, end, hoop.thickness, color)
}

private fun visualizeStudEarring(
    state: CharacterRenderState,
    stud: StudEarring,
    position: Point2d,
    earRadius: Distance,
) {
    val radius = state.config.equipment.earring.calculateStudSize(earRadius, stud.size)
    val renderer = state.renderer.getLayer(EQUIPMENT_LAYER)

    visualizeOrnament(state, renderer, stud.ornament, position, radius)
}

private fun visualizeEarringWire(
    state: CharacterRenderState,
    earRadius: Distance,
    top: Point2d,
    bottom: Point2d,
    thickness: Size,
    color: Color,
) = visualizeWire(
    state.getLayer(EQUIPMENT_LAYER - 1),
    top,
    bottom,
    state.config.equipment.earring.calculateWireThickness(earRadius, thickness),
    color,
)

fun calculateMaxDrop(
    aabb: AABB,
    start: Point2d,
    earRadius: Distance,
): Distance {
    val minEnd = start.addHeight(earRadius)
    return aabb.getEnd().y - minEnd.y
}
