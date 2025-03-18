package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderConfig

class PaddedSize(
    val baseSize: Size2d,
    var top: Float = 0.0f,
    var bottom: Float = 0.0f,
    var left: Float = 0.0f,
    var right: Float = 0.0f,
) {
    fun add(padding: Distance) {
        val meters = padding.toMeters()
        top += meters
        bottom += meters
        left += meters
        right += meters
    }

    fun addToTopAndSide(padding: Distance) {
        val meters = padding.toMeters()
        top += meters
        left += meters
        right += meters
    }

    fun getFullSize() = baseSize + Size2d(left + right, top + bottom)
    fun getInnerAABB() = AABB(left, top, baseSize)
}

fun calculateSize(config: CharacterRenderConfig, appearance: Appearance): PaddedSize {
    val padded = when (appearance) {
        is HeadOnly -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            handleHead(config, appearance.head, padded)
            padded
        }

        is HumanoidBody -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            handleHead(config, appearance.head, padded)
            padded
        }

        UndefinedAppearance -> PaddedSize(Size2d.square(config.padding * 2.0f))
    }

    padded.add(config.padding)

    return padded
}

private fun handleHead(config: CharacterRenderConfig, head: Head, paddedSize: PaddedSize) {
    handleHorn(config, head.horns, paddedSize)
}

private fun handleHorn(config: CharacterRenderConfig, horns: Horns, paddedSize: PaddedSize) {
    val headHeight = Distance.fromMeters(paddedSize.baseSize.height) * config.body.headHeight

    when (horns) {
        NoHorns -> doNothing()
        is TwoHorns -> paddedSize.addToTopAndSide(horns.horn.calculatePadding(headHeight))
        is DifferentHorns -> {
            val bonus = horns.left.calculatePadding(headHeight)
                .max(horns.right.calculatePadding(headHeight))
            paddedSize.addToTopAndSide(bonus)
        }

        is CrownOfHorns -> {
            val bonus = headHeight * horns.length
            paddedSize.top += bonus.toMeters()
        }
    }
}
