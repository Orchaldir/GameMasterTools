package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        "ears.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(EarShape.entries),
    ) { distance, tail, size ->
        Pair(createAppearance(distance, tail, size), emptyList())
    }
}

private fun createAppearance(height: Distance, earShape: EarShape, size: Size) =
    HeadOnly(Head(ears = NormalEars(earShape, size), eyes = TwoEyes(), skin = ExoticSkin()), height)
