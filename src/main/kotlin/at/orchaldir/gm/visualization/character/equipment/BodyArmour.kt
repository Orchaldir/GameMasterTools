package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.ChainMail
import at.orchaldir.gm.core.model.item.equipment.style.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.part.visualizeChainMail
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLamellarArmour
import at.orchaldir.gm.visualization.character.equipment.part.visualizeScaleArmour
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSegmentedArmour

fun visualizeBodyArmour(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
) = when (armour.style) {
    is ChainMail -> visualizeChainMail(state, body, armour, armour.style)
    is LamellarArmour -> visualizeLamellarArmour(state, body, armour, armour.style)
    is ScaleArmour -> visualizeScaleArmour(state, body, armour, armour.style)
    is SegmentedArmour -> visualizeSegmentedArmour(state, body, armour, armour.style)
}

