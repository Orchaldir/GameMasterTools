package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.NoPolearmHead
import at.orchaldir.gm.core.model.item.equipment.style.SharpenedPolearmHead
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShaft
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.VerticalStripesLookup
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val heads = listOf(
        NoPolearmHead,
        SharpenedPolearmHead,
    )
    val stripes = VerticalStripesLookup(Color.Red, Color.Gold)
    val shafts = listOf(
        SimpleShaft(FillLookupItemPart(Color.SaddleBrown)),
        SimpleShaft(FillLookupItemPart(fill = stripes)),
    )

    renderCharacterTableWithoutColorScheme(
        State(),
        "polearms.svg",
        CHARACTER_CONFIG,
        addNames(shafts),
        addNames(heads),
    ) { distance, head, shaft ->
        val polearm = Polearm(
            head,
            shaft
        )
        Pair(createAppearance(distance), from(polearm))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )