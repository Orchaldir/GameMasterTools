package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.ScaleArmour
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLamellarArmour
import at.orchaldir.gm.visualization.character.equipment.part.visualizeScaleArmour

fun visualizeBodyArmour(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
) = when (armour.style) {
    is LamellarArmour -> visualizeLamellarArmour(state, body, armour, armour.style)
    is ScaleArmour -> visualizeScaleArmour(state, body, armour, armour.style)
}

