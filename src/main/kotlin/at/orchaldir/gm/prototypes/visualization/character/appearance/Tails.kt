package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.wing.NoWings
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTable(
        State(),
        "tails.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        addNames(SimpleTailShape.entries),
        true,
    ) { distance, tail, size ->
        Pair(createAppearance(distance, tail, size), EquipmentMap())
    }
}

private fun createAppearance(distance: Distance, tailShape: SimpleTailShape, size: Size) =
    HumanoidBody(
        Body(),
        Head(eyes = TwoEyes()),
        distance,
        NormalSkin(),
        SimpleTail(tailShape, size, OverwriteFeatureColor(ExoticSkin(Color.Blue))),
        NoWings,
    )