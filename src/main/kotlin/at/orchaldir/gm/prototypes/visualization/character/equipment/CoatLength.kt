package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.ColorSchemeItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.Button
import at.orchaldir.gm.core.model.item.equipment.style.ButtonColumn
import at.orchaldir.gm.core.model.item.equipment.style.DoubleBreasted
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.util.render.Color.Blue
import at.orchaldir.gm.core.model.util.render.Color.Gold
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(),
        "coat-length.svg",
        CHARACTER_CONFIG,
        addNames(OuterwearLength.entries),
        addNames(BodyShape.entries)
    ) { distance, shape, length ->
        val coat = Coat(
            length,
            openingStyle = DoubleBreasted(ButtonColumn(Button(Size.Medium, ColorSchemeItemPart(Gold)), 5u)),
            main = FillItemPart(Blue),
        )
        Pair(createAppearance(distance, shape), from(coat))
    }
}

private fun createAppearance(distance: Distance, shape: BodyShape) =
    HumanoidBody(
        Body(shape, NormalFoot, Size.Medium),
        Head(),
        distance,
    )