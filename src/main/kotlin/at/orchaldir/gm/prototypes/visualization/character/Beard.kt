package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val appearances = mutableListOf<List<Appearance>>()
    val styles = mutableListOf<BeardStyle>()

    MoustacheStyle.entries.forEach { styles.add(Moustache(it)) }
    GoateeStyle.entries.forEach { styles.add(Goatee(it)) }

    styles.forEach { style ->
        val row = mutableListOf<Appearance>()

        Size.entries.forEach {
            row.add(createAppearance(style, OneEye(size = it)))
        }
        row.add(createAppearance(style, TwoEyes()))

        appearances.add(row)
    }

    renderTable("beard.svg", RENDER_CONFIG, appearances)
}

private fun createAppearance(style: BeardStyle, eyes: Eyes) =
    HeadOnly(
        Head(
            NormalBeard(style, Color.SaddleBrown),
            NormalEars(),
            eyes,
            NormalHair(SidePart(Side.Left), Color.SaddleBrown),
            SimpleMouth()
        ), Distance(0.2f)
    )