package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

val HAIR_STYLES: List<HairStyle> = listOf(
    BowlCut,
    BuzzCut,
    FlatTop,
    MiddlePart,
    ShavedHair,
    SidePart(Side.Left),
    SidePart(Side.Right),
    Spiked,
)

val EYES: List<Pair<String, Eyes>> = listOf(
    Pair("One Small", OneEye(size = Size.Small)),
    Pair("One Medium", OneEye(size = Size.Small)),
    Pair("One Large", OneEye(size = Size.Small)),
    Pair("Two", TwoEyes()),
)

fun main() {
    renderCharacterTable(
        "hair-short.svg",
        CHARACTER_CONFIG,
        addNames(HAIR_STYLES),
        EYES,
        false,
    ) { distance, eyes, style ->
        Pair(createAppearance(distance, eyes, style), emptyList())
    }
}

private fun createAppearance(height: Distance, eyes: Eyes, style: HairStyle) =
    HeadOnly(
        Head(NormalEars(), eyes, NormalHair(style, Color.SaddleBrown), NoHorns, NormalMouth()),
        height,
    )