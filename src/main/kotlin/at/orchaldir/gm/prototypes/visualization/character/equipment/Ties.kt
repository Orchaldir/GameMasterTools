package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "ties.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(TieStyle.entries)
    ) { distance, style, size ->
        val tie = Tie(
            style,
            size,
            Solid(Color.Yellow),
            Solid(Color.Red),
        )
        Pair(
            createAppearance(distance),
            EquipmentMap(
                mapOf(
                    BodySlot.TieSlot to tie,
                    BodySlot.BeltSlot to Belt(),
                    BodySlot.BottomSlot to Pants(),
                    BodySlot.TopSlot to Shirt(),
                ),
            )
        )
    }
}

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(),
        Head(mouth = NormalMouth()),
        distance,
    )