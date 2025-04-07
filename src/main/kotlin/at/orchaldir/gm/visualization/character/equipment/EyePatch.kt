package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.EyesConfig

private val xPair = Pair(START, END)

data class EyePatchConfig(
    val fixationSize: SizeConfig<Factor>,
    val fixationDeltaY: Factor,
) {
    fun getFixationOptions(aabb: AABB, color: Color, size: Size) =
        LineOptions(color.toRender(), aabb.convertHeight(fixationSize.convert(size)))

    fun getOneBandPoints(eyesConfig: EyesConfig, aabb: AABB, side: Side) = Pair(
        aabb.getPoint(side.flip().get(xPair), eyesConfig.twoEyesY),
        aabb.getPoint(side.get(xPair), eyesConfig.twoEyesY - fixationDeltaY)
    )

    fun getDiagonalBandPoints(eyesConfig: EyesConfig, aabb: AABB, side: Side) = Pair(
        aabb.getPoint(side.flip().get(xPair), eyesConfig.twoEyesY - fixationDeltaY),
        aabb.getPoint(side.get(xPair), eyesConfig.twoEyesY + fixationDeltaY)
    )
}

fun visualizeEyePatch(
    state: CharacterRenderState,
    head: Head,
    eyePatch: EyePatch,
) {
    val side = Side.Right

    if (state.renderFront) {
        visualizeEyePatchForTwoEyes(state, side, eyePatch)
    } else {
        visualizeEyePatchForTwoEyesAndBehind(state, side, eyePatch)
    }

}

fun visualizeEyePatchForTwoEyes(
    state: CharacterRenderState,
    side: Side,
    eyePatch: EyePatch,
) {
    val center = side
        .flip()
        .get(state.config.head.eyes.getTwoEyesCenter(state.aabb))

    visualizeFixationForTwoEyes(state, center, side, eyePatch.fixation)

    when (eyePatch.style) {
        is SimpleEyePatch -> {
            val options = state.config.getLineOptions(eyePatch.style.color)
            visualizeLens(state, options, center, eyePatch.style.shape)
        }

        is OrnamentAsEyePatch -> doNothing()
    }
}

fun visualizeEyePatchForTwoEyesAndBehind(
    state: CharacterRenderState,
    side: Side,
    eyePatch: EyePatch,
) {
    visualizeFixationForTwoEyesAndBehind(state, side.flip(), eyePatch.fixation)
}

private fun visualizeFixationForTwoEyes(
    state: CharacterRenderState,
    center: Point2d,
    side: Side,
    fixation: EyePatchFixation,
) {
    val eyesConfig = state.config.head.eyes
    val eyePatchConfig = state.config.equipment.eyePatch
    val renderer = state.renderer.getLayer(EQUIPMENT_LAYER)
    val offsetY = state.aabb.convertHeight(eyePatchConfig.fixationDeltaY) / 2.0f

    when (fixation) {
        NoFixation -> doNothing()
        is OneBand -> {
            val (closeEnd, distantEnd) = eyePatchConfig.getOneBandPoints(eyesConfig, state.aabb, side)
            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, fixation.size)

            renderer.renderLine(listOf(closeEnd, center, distantEnd), options)
        }

        is DiagonalBand -> {
            val (closeEnd, distantEnd) = eyePatchConfig.getDiagonalBandPoints(eyesConfig, state.aabb, side)
            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, fixation.size)

            renderer.renderLine(listOf(closeEnd, center, distantEnd), options)
        }
        is TwoBands -> {
            val (topLeft, topRight) = state.aabb
                .getMirroredPoints(FULL, eyesConfig.twoEyesY - eyePatchConfig.fixationDeltaY)
            val (bottomLeft, bottomRight) = state.aabb
                .getMirroredPoints(FULL, eyesConfig.twoEyesY + eyePatchConfig.fixationDeltaY)

            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, Size.Small)

            renderer.renderLine(listOf(topLeft, center.minusHeight(offsetY), topRight), options)
            renderer.renderLine(listOf(bottomLeft, center.addHeight(offsetY), bottomRight), options)
        }
    }
}

private fun visualizeFixationForTwoEyesAndBehind(
    state: CharacterRenderState,
    side: Side,
    fixation: EyePatchFixation,
) {
    val eyesConfig = state.config.head.eyes
    val eyePatchConfig = state.config.equipment.eyePatch
    val renderer = state.renderer.getLayer(EQUIPMENT_LAYER)

    when (fixation) {
        NoFixation, is TwoBands -> doNothing()
        is OneBand -> {
            val (closeEnd, distantEnd) = eyePatchConfig.getOneBandPoints(eyesConfig, state.aabb, side)
            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, fixation.size)

            renderer.renderLine(listOf(closeEnd, distantEnd), options)
        }

        is DiagonalBand -> {
            val (closeEnd, distantEnd) = eyePatchConfig.getDiagonalBandPoints(eyesConfig, state.aabb, side)
            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, fixation.size)

            renderer.renderLine(listOf(closeEnd, distantEnd), options)
        }

    }
}