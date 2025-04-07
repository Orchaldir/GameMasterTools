package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentAsEyePatch
import at.orchaldir.gm.core.model.item.equipment.style.SimpleEyePatch
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class EyePatchConfig(
    val fixationSize: SizeConfig<Factor>,
)

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
    if (!state.renderFront) {
        return
    }

    val center = side.get(state.config.head.eyes.getTwoEyesCenter(state.aabb))

    when (eyePatch.style) {
        is SimpleEyePatch -> {
            val options = NoBorder(eyePatch.style.color.toRender())
            visualizeLens(state, options, center, eyePatch.style.shape)
        }

        is OrnamentAsEyePatch -> doNothing()
    }
}
