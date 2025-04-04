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

    fun addToSide(padding: Distance) {
        val meters = padding.toMeters()
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
    fun getFullAABB() = AABB(getFullSize())
    fun getInnerAABB() = AABB(left, top, baseSize)
}

fun calculateSize(config: CharacterRenderConfig, appearance: Appearance): PaddedSize {
    val padded = when (appearance) {
        is HeadOnly -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            handleHead(config, appearance.head, padded, appearance.height)
            padded
        }

        is HumanoidBody -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            val headHeight = appearance.height * config.body.headHeight
            handleHead(config, appearance.head, padded, headHeight)
            padded
        }

        UndefinedAppearance -> PaddedSize(Size2d.square(config.padding * 2.0f))
    }

    padded.add(config.padding)

    return padded
}

private fun handleHead(
    config: CharacterRenderConfig,
    head: Head,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {
    handleEars(config, head.ears, paddedSize, headHeight)
    handleHorns(config, head.horns, paddedSize, headHeight)
}

private fun handleEars(
    config: CharacterRenderConfig,
    ears: Ears,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {

    when (ears) {
        NoEars -> doNothing()
        is NormalEars -> when (ears.shape) {
            EarShape.PointedSideways -> doNothing()
            EarShape.PointedUpwards -> doNothing()
            EarShape.Round -> {
                val earRadius = config.head.ears.getRoundRadius(headHeight, ears.size)
                paddedSize.addToSide(earRadius)
            }
        }
    }
}

private fun handleHorns(
    config: CharacterRenderConfig,
    horns: Horns,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {

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
