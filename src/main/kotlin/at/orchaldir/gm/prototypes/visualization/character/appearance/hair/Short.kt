package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

val EYES: List<Pair<String, Eyes>> = listOf(
    createOneEye(Size.Small),
    createOneEye(Size.Medium),
    createOneEye(Size.Large),
    Pair("Two", TwoEyes()),
)

private fun createOneEye(size: Size) = Pair("One $size", OneEye(size = size))

fun main() {
    renderCharacterTable(
        "hair-short.svg",
        CHARACTER_CONFIG,
        addNames(ShortHairStyle.entries),
        EYES,
        false,
    ) { distance, eyes, style ->
        Pair(createAppearance(distance, eyes, style), emptyList())
    }
}

private fun createAppearance(height: Distance, eyes: Eyes, style: ShortHairStyle) =
    HeadOnly(
        Head(NormalEars(), eyes, NormalHair(ShortHairCut(style), Color.SaddleBrown), NoHorns, NormalMouth()),
        height,
    )