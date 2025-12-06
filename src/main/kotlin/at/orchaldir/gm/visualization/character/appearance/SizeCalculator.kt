package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentElementMap
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.item.equipment.style.ChainmailHood
import at.orchaldir.gm.core.model.item.equipment.style.GreatHelm
import at.orchaldir.gm.core.model.item.equipment.style.HelmetShape
import at.orchaldir.gm.core.model.item.equipment.style.SkullCap
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.equipment.HelmetConfig

class PaddedSize(
    val baseSize: Size2d,
    var universial: Distance = ZERO_DISTANCE,
    var top: Distance = ZERO_DISTANCE,
    var bottom: Distance = ZERO_DISTANCE,
    var left: Distance = ZERO_DISTANCE,
    var right: Distance = ZERO_DISTANCE,
) {
    fun addUniversial(padding: Distance) {
        universial += padding
    }

    fun addToSide(padding: Distance) {
        left += padding
        right += padding
    }

    fun addToTop(padding: Distance) {
        top += padding
    }

    fun addToTopAndSide(padding: Distance) {
        top += padding
        left += padding
        right += padding
    }

    fun getInnerSize() = baseSize.addWidth(left + right).addHeight(top + bottom)
    fun getInnerAABB() = AABB(Point2d.square(universial), getInnerSize())
    fun getFullSize() = getInnerSize() + universial * 2.0f
    fun getFullAABB() = AABB(getFullSize())
}

fun calculatePaddedSize(
    config: CharacterRenderConfig,
    appearance: Appearance,
    equipmentMap: EquipmentElementMap = EquipmentElementMap(),
): PaddedSize {
    val padded = when (appearance) {
        is HeadOnly -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            handleHead(config, appearance.head, equipmentMap, padded, appearance.height)
            padded
        }

        is HumanoidBody -> {
            val padded = PaddedSize(Size2d.square(appearance.height))
            val headHeight = appearance.height * config.body.headHeight
            handleHead(config, appearance.head, equipmentMap, padded, headHeight)
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
    equipmentMap: EquipmentElementMap,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {
    handleEars(config, head.ears, paddedSize, headHeight)
    handleHorns(config, head.horns, paddedSize, headHeight)
    handleHeadEquipment(config, equipmentMap, paddedSize, headHeight)
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
            paddedSize.top += bonus
        }
    }
}

private fun handleHeadEquipment(
    config: CharacterRenderConfig,
    equipmentMap: EquipmentElementMap,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {
    equipmentMap.getAllEquipment().forEach { (data, _) ->
        when (data) {
            is Helmet -> {
                val helmet = config.equipment.helmet

                when (data.style) {
                    is ChainmailHood -> doNothing()
                    is GreatHelm -> handleHelmetShape(helmet, data.style.shape, paddedSize, headHeight)
                    is SkullCap -> handleHelmetShape(helmet, data.style.shape, paddedSize, headHeight)
                }
            }
            is IounStone -> {
                val config = config.equipment.iounStone
                paddedSize.addToTopAndSide(headHeight * config.orbitY)
            }
            else -> doNothing()
        }
    }
}

private fun handleHelmetShape(
    config: HelmetConfig,
    shape: HelmetShape,
    paddedSize: PaddedSize,
    headHeight: Distance,
) {
    val padding = headHeight * when (shape) {
        HelmetShape.Bucket, HelmetShape.Round -> config.getRoundTopPadding()
        HelmetShape.Cone, HelmetShape.RoundedCone -> config.getConicalTopPadding()
        HelmetShape.Onion -> config.getOnionTopPadding()
    }

    paddedSize.addToTop(padding)
}
