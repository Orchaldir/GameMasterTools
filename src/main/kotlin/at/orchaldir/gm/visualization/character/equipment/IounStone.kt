package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class IounStoneConfig(
    val fixationSize: SizeConfig<Factor>,
    val fixationDeltaY: Factor,
    val ornamentRadius: Factor,
    val teardropRadius: Factor,
)

fun visualizeIounStone(
    state: CharacterRenderState,
    stone: IounStone,
    set: Set<BodySlot>,
) {

}
