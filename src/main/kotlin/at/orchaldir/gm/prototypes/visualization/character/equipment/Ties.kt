package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.app.PANTS
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle.None
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.core.model.item.equipment.style.TieStyle
import at.orchaldir.gm.core.model.util.Color.Blue
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
        Pair(
            createAppearance(distance),
            listOf(
                Tie(style, size),
                Belt(),
                Pants(),
                Shirt(),
            ),
        )
    }
}

private fun createAppearance(distance: Distance) =
    HumanoidBody(
        Body(),
        Head(),
        distance,
    )