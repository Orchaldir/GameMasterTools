package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

val HAIR_STYLES: List<HairStyle> = listOf(
    BuzzCut,
    FlatTop,
    MiddlePart,
    ShavedHair,
    SidePart(Side.Left),
    SidePart(Side.Right),
    Spiked,
)

fun main() {
    val appearances = mutableListOf<List<Appearance>>()

    HAIR_STYLES.forEach { style ->
        val row = mutableListOf<Appearance>()

        Size.entries.forEach {
            row.add(createAppearance(style, OneEye(size = it)))
        }
        row.add(createAppearance(style, TwoEyes()))

        appearances.add(row)
    }

    renderCharacterTable("hair.svg", CHARACTER_CONFIG, appearances)
}

private fun createAppearance(style: HairStyle, eyes: Eyes) =
    HeadOnly(Head(NormalEars(), eyes, NormalHair(style, Color.SaddleBrown), NoHorns, NormalMouth()), Distance(200))