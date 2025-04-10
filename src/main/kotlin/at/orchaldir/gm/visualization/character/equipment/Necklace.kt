package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class NecklaceConfig(
    private val lengthMap: Map<NecklaceLength, Factor>,
) {
    fun getLength(length: NecklaceLength) = lengthMap.getValue(length)
}

fun visualizeNecklace(
    state: CharacterRenderState,
    body: Body,
    necklace: Necklace,
) {
    val length = state.config.equipment.necklace.getLength(necklace.length)

    when (necklace.style) {
        is DangleNecklace -> doNothing()
        is DropNecklace -> doNothing()
        is PendantNecklace -> doNothing()
        is StrandNecklace -> visualizeStrandNecklace(state, necklace.style, length)
    }
}

private fun visualizeStrandNecklace(
    state: CharacterRenderState,
    necklace: StrandNecklace,
    length: Factor,
) {

}
