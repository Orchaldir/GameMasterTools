package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Ears
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.NoEars
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.style.StudEarring
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HeadConfig

data class EarringConfig(
    val studSize: SizeConfig<Factor>,
) {

    fun calculatePosition(aabb: AABB, head: HeadConfig, ears: Ears) = when (ears) {
        NoEars -> error("Earrings require ears!")
        is NormalEars -> {
            val (left, right) = aabb.getMirroredPoints(FULL, head.earY)
            val radius = head.ears.getRoundRadius(aabb, ears.size)
            val leftOffset = Point2d(radius.toMeters(), -radius.toMeters())
            val rightOffset = Point2d(radius.toMeters(), radius.toMeters())

            Pair(left + leftOffset, right + rightOffset)
        }
    }
}

fun visualizeEarrings(
    state: CharacterRenderState,
    head: Head,
    earring: Earring,
) {
    val (left, right) = state.config.equipment.earring.calculatePosition(state.aabb, state.config.head, head.ears)

    visualizeEarring(state, earring, left)
    visualizeEarring(state, earring, right)
}

private fun visualizeEarring(
    state: CharacterRenderState,
    earring: Earring,
    position: Point2d,
) {
    when (earring.style) {
        is StudEarring -> visualizeStudEarring(state, earring, earring.style, position)
    }
}

private fun visualizeStudEarring(
    state: CharacterRenderState,
    earring: Earring,
    stud: StudEarring,
    position: Point2d,
) {
    val options = NoBorder(earring.color.toRender())
    val radius = state.aabb.convertHeight(state.config.equipment.earring.studSize.convert(stud.size))

    state.renderer.getLayer()
        .renderCircle(position, radius, options)
}
