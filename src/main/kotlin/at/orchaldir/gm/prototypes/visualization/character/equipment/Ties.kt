package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsKeyMap
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "ties.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(TieStyle.entries)
    ) { distance, style, size ->
        val tie = Tie(
            style,
            size,
            Color.Yellow,
            Color.Red,
        )
        Pair(
            createAppearance(distance),
            fromSlotAsKeyMap(
                mapOf(
                    BodySlot.Neck to tie,
                    BodySlot.Belt to Belt(),
                    BodySlot.Bottom to Pants(),
                    BodySlot.Top to Shirt(),
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