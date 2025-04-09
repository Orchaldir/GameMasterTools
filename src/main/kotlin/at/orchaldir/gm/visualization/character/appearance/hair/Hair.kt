package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class HairConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
    val width: Factor,
    val longPadding: Factor,
    private val lengthMap: Map<HairLength, Factor>,
    val braidWidth: Factor,
    val ponytailWidth: Factor,
    val ponytailWideWidth: Factor,
) {
    fun getLength(length: HairLength) = lengthMap.getOrDefault(length, FULL)

    fun getBottomWidth(style: PonytailStyle) = when (style) {
        PonytailStyle.Wide -> ponytailWideWidth
        else -> ponytailWidth
    }
}

fun visualizeHair(state: CharacterRenderState, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is NormalHair -> visualizeNormalHair(state, head.hair)
    }
}

private fun visualizeNormalHair(state: CharacterRenderState, hair: NormalHair) {
    when (hair.cut) {
        is LongHairCut -> visualizeLongHair(state, hair, hair.cut)
        is ShortHairCut -> visualizeShortHair(state, hair, hair.cut)
        is Ponytail -> visualizePonytail(state, hair, hair.cut)
    }
}
