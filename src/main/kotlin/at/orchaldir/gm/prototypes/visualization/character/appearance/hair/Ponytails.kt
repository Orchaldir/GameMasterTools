package at.orchaldir.gm.prototypes.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.FemaleMouth
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "hair-ponytail.svg",
        CHARACTER_CONFIG,
        addNames(PonytailStyle.entries),
        addNames(PonytailPosition.entries),
        true,
    ) { distance, position, style ->
        Pair(createAppearance(distance, position, style), EquipmentMap())
    }
}

private fun createAppearance(height: Distance, position: PonytailPosition, style: PonytailStyle) =
    HumanoidBody(
        Body(BodyShape.Hourglass),
        Head(
            NormalEars(),
            TwoEyes(),
            NormalHair(Ponytail(style, position, HairLength.Knee), Color.Gold),
            NoHorns,
            FemaleMouth()
        ),
        height,
    )