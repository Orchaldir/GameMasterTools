package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class GlassesConfig(
    val size: SizeConfig<Factor>,
)

fun visualizeGlasses(
    state: CharacterRenderState,
    head: Head,
    glasses: Glasses,
) {

}
