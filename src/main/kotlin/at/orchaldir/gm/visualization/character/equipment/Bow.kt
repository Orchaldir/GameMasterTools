package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
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
    bow: Bow,
) {
    val height = state.fullAABB.convertHeight(bow.height)

    visualizeBowShape(state, bow)
}

fun visualizeBowShape(
    state: CharacterRenderState<Body>,
    bow: Bow,
) {
    when (bow.shape) {
        BowShape.Angular -> doNothing()
        BowShape.Straight -> doNothing()
    }
}


