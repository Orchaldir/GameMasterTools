package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.equipment.part.*

data class BowConfig(
    val thicknessCenter: Factor,
)

fun visualizeBow(
    state: CharacterRenderState<Body>,
    armour: Bow,
) {

}

fun visualizeBowShape(
    state: CharacterRenderState<Body>,
    armour: Bow,
) {

}


