package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val appearances = mutableListOf<List<Appearance>>()

    ShortHairStyle.entries.forEach { style ->
        val row = mutableListOf<Appearance>()

        Size.entries.forEach() {
            row.add(createAppearance(style, OneEye(size = it)))
        }
        row.add(createAppearance(style, TwoEyes()))

        appearances.add(row)
    }

    renderTable("hair_short.svg", RENDER_CONFIG, appearances)
}

private fun createAppearance(style: ShortHairStyle, eyes: Eyes) =
    HeadOnly(Head(NormalEars(), eyes, ShortHair(style), SimpleMouth()), Distance(0.2f))