package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Side
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle.VanDyke
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle.Handlebar
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.SidePart
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val beards = mutableListOf<BeardStyle>()

    MoustacheStyle.entries.forEach { beards.add(Moustache(it)) }
    GoateeStyle.entries.forEach { beards.add(Goatee(it)) }
    beards.add(GoateeAndMoustache(Handlebar, VanDyke))

    val eyes = mutableListOf<Eyes>()

    Size.entries.forEach {
        eyes.add(OneEye(size = it))
    }
    eyes.add(TwoEyes())

    renderTable(
        "beard.svg",
        RENDER_CONFIG,
        addNamesToBeardStyle(beards),
        addNamesToEyes(eyes)
    ) { distance, eyes, beard ->
        createAppearance(distance, eyes, beard)
    }
}

private fun createAppearance(distance: Distance, eyes: Eyes, style: BeardStyle) =
    HeadOnly(
        Head(
            NormalEars(),
            eyes,
            NormalHair(SidePart(Side.Left), Color.SaddleBrown),
            NormalMouth(NormalBeard(style, Color.SaddleBrown))
        ),
        distance,
    )