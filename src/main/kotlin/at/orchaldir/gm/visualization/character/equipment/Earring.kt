package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class EarringConfig(
    val studSize: SizeConfig<Factor>,
)

fun visualizeEarring(
    state: CharacterRenderState,
    head: Head,
    earring: Earring,
) {

}
