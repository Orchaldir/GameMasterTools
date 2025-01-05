package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle.VanDyke
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle.Handlebar
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.SidePart
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.addNamesToBeardStyle
import at.orchaldir.gm.prototypes.visualization.character.addNamesToEyes
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
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

    renderCharacterTable(
        "beard.svg",
        CHARACTER_CONFIG,
        addNamesToBeardStyle(beards),
        addNamesToEyes(eyes),
    ) { distance, eyes, beard ->
        Pair(createAppearance(distance, eyes, beard), emptyList())
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