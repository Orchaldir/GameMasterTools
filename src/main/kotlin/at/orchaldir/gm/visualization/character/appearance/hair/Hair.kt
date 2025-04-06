package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class HairConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
    val width: Factor,
)

fun visualizeHair(state: CharacterRenderState, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is NormalHair -> visualizeNormalHair(state, head.hair)
    }
}

private fun visualizeNormalHair(state: CharacterRenderState, hair: NormalHair) {
    when (hair.cut) {
        is ShortHairCut -> visualizeShortHair(state, hair, hair.cut)
        is LongHairCut -> visualizeLongHair(state, hair, hair.cut)
    }
}
