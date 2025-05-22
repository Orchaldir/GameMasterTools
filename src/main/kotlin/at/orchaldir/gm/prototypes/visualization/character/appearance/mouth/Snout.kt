package at.orchaldir.gm.prototypes.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.mouth.Snout
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.EYES
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "snouts.svg",
        CHARACTER_CONFIG,
        addNames(SnoutShape.entries),
        EYES,
        false,
    ) { distance, eyes, shape ->
        Pair(createAppearance(distance, eyes, shape), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, eyes: Eyes, shape: SnoutShape) =
    HeadOnly(
        Head(
            eyes = eyes,
            mouth = Snout(shape, Color.Pink),
        ),
        height,
        ExoticSkin(),
    )
