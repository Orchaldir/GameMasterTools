package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderConfig

class PaddedSize(
    val baseSize: Size2d,
    var universial: Float = 0.0f,
    var top: Float = 0.0f,
    var bottom: Float = 0.0f,
    var left: Float = 0.0f,
    var right: Float = 0.0f,
) {
    fun addUniversial(padding: Distance) {
        val meters = padding.toMeters()
        universial += meters
    }

    fun addToSide(padding: Distance) {
        val meters = padding.toMeters()
        left += meters
        right += meters
    }

    fun addToTop(padding: Distance) {
        val meters = padding.toMeters()
        top += meters
    }

    fun addToTopAndSide(padding: Distance) {
        val meters = padding.toMeters()
        top += meters
        left += meters
        right += meters
    }

    fun getInnerSize() = baseSize.addWidth(left + right).addHeight(top + bottom)
    fun getInnerAABB() = AABB(Point2d.square(universial), getInnerSize())
    fun getFullSize() = getInnerSize() + Distance.fromMeters(2.0f * universial)
    fun getFullAABB() = AABB(getFullSize())
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

    padded.addUniversial(config.padding)

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
            EarShape.PointedSideways -> {
                val earLength = config.head.ears.getSidewaysLength(headHeight, ears.size)
                paddedSize.addToSide(earLength)
            }

            EarShape.PointedUpwards -> {
                val earLength = config.head.ears.getUpwardsLength(headHeight, ears.size)
                val earPosition = headHeight * config.head.earY

                if (earLength > earPosition) {
                    val heightAboveHead = earLength - earPosition
                    paddedSize.addToTop(heightAboveHead)
                }
            }
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
