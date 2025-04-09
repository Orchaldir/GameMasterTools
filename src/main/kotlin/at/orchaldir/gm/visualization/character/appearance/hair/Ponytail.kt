package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.Ponytail
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizePonytail(state: CharacterRenderState, hair: NormalHair, ponytail: Ponytail) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val height = config.getHairLength(state.aabb, ponytail.length)


}
