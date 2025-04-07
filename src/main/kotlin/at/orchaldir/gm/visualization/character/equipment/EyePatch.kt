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

data class EyePatchConfig(
    val fixationSize: SizeConfig<Factor>,
    val fixationDeltaY: Factor,
) {
    fun getFixationOptions(aabb: AABB, color: Color, size: Size) =
        LineOptions(color.toRender(), aabb.convertHeight(fixationSize.convert(size)))
}

fun visualizeEyePatch(
    state: CharacterRenderState,
    head: Head,
    eyePatch: EyePatch,
) {
    if (!state.renderFront) {
        return
    }

    visualizeEyePatchForTwoEyes(state, Side.Right, eyePatch)
}

fun visualizeEyePatchForTwoEyes(
    state: CharacterRenderState,
    side: Side,
    eyePatch: EyePatch,
) {
    val center = side
        .flip()
        .get(state.config.head.eyes.getTwoEyesCenter(state.aabb))

    visualizeFixation(state, center, side, eyePatch.fixation)

    when (eyePatch.style) {
        is SimpleEyePatch -> {
            val options = state.config.getLineOptions(eyePatch.style.color)
            visualizeLens(state, options, center, eyePatch.style.shape)
        }

        is OrnamentAsEyePatch -> doNothing()
    }
}

private fun visualizeFixation(
    state: CharacterRenderState,
    center: Point2d,
    side: Side,
    fixation: EyePatchFixation,
) {
    val eyesConfig = state.config.head.eyes
    val eyePatchConfig = state.config.equipment.eyePatch
    val renderer = state.getLayer(EQUIPMENT_LAYER)
    val offset = state.aabb.convertHeight(eyePatchConfig.fixationDeltaY) / 2.0f

    when (fixation) {
        NoFixation -> doNothing()
        is OneBand -> {
            val xPair = Pair(START, END)
            val closeEnd = state.aabb.getPoint(side.flip().get(xPair), eyesConfig.twoEyesY)
            val distantEnd = state.aabb.getPoint(side.get(xPair), eyesConfig.twoEyesY - eyePatchConfig.fixationDeltaY)
            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, fixation.size)

            renderer.renderLine(listOf(closeEnd, center), options)
            renderer.renderLine(listOf(distantEnd, center), options)
        }

        is DiagonalBand -> doNothing()
        is TwoBands -> {
            val (topLeft, topRight) = state.aabb
                .getMirroredPoints(FULL, eyesConfig.twoEyesY - eyePatchConfig.fixationDeltaY)
            val (bottomLeft, bottomRight) = state.aabb
                .getMirroredPoints(FULL, eyesConfig.twoEyesY + eyePatchConfig.fixationDeltaY)

            val options = eyePatchConfig.getFixationOptions(state.aabb, fixation.color, Size.Small)

            renderer.renderLine(listOf(topLeft, center.minusHeight(offset), topRight), options)
            renderer.renderLine(listOf(bottomLeft, center.addHeight(offset), bottomRight), options)
        }
    }
}
