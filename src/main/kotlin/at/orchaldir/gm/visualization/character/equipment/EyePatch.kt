package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.utils.math.Factor
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


}
