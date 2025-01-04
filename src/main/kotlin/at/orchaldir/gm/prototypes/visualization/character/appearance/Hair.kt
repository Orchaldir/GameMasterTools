package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.RENDER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderTable
import at.orchaldir.gm.utils.math.Distance

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

    renderTable("hair.svg", RENDER_CONFIG, appearances)
}

private fun createAppearance(style: HairStyle, eyes: Eyes) =
    HeadOnly(Head(NormalEars(), eyes, NormalHair(style, Color.SaddleBrown), NormalMouth()), Distance(200))