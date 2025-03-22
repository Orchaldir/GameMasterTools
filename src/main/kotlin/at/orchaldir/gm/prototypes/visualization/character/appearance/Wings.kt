package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf<List<Appearance>>()

    appearances.add(WingType.entries.map { createAppearance(OneWing(createWing(it), Side.Left)) })
    appearances.add(WingType.entries.map { createAppearance(OneWing(createWing(it), Side.Right)) })
    appearances.add(WingType.entries.map { createAppearance(TwoWings(createWing(it))) })
    appearances.add(
        listOf(
            createAppearance(DifferentWings(BatWing(), BirdWing())),
            createAppearance(DifferentWings(BirdWing(), ButterflyWing())),
            createAppearance(DifferentWings(ButterflyWing(), BatWing())),
        )
    )

    renderCharacterTable("wings.svg", CHARACTER_CONFIG, appearances)
}

private fun createWing(type: WingType) = when (type) {
    WingType.Bat -> BatWing()
    WingType.Bird -> BirdWing()
    WingType.Butterfly -> ButterflyWing()
}

private fun createAppearance(wings: Wings) =
    HumanoidBody(
        Body(),
        Head(),
        fromMillimeters(2000),
        wings,
    )