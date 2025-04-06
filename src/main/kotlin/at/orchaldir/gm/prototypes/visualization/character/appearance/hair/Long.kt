package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val styles = mutableListOf<Pair<LongHairShape, LongHairStyle>>()

    LongHairShape.entries.forEach { shape ->
        LongHairStyle.entries.forEach { style ->
            styles.add(Pair(shape, style))
        }
    }

    renderCharacterTable(
        "hair-long.svg",
        CHARACTER_CONFIG,
        addNames(HairLength.entries),
        addNames(styles),
        true,
    ) { distance, (shape, style), length ->
        Pair(createAppearance(distance, length, shape, style), emptyList())
    }
}

private fun createAppearance(height: Distance, length: HairLength, shape: LongHairShape, style: LongHairStyle) =
    HumanoidBody(
        Body(),
        Head(
            NormalEars(),
            TwoEyes(),
            NormalHair(LongHairCut(style, shape, length), Color.SaddleBrown),
            NoHorns,
            NormalMouth()
        ),
        height,
    )