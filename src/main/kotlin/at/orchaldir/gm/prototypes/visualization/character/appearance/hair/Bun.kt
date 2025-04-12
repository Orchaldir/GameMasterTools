package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.FemaleMouth
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "hair-bun.svg",
        CHARACTER_CONFIG,
        addNames(BunStyle.entries),
        addNames(Size.entries),
        true,
    ) { distance, size, style ->
        Pair(createAppearance(distance, style, size), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, style: BunStyle, size: Size) =
    HeadOnly(
        Head(
            NormalEars(),
            TwoEyes(),
            NormalHair(Bun(style, size), Color.SaddleBrown),
            NoHorns,
            FemaleMouth()
        ),
        height,
    )