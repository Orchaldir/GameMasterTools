package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.PolearmHeadWithSpearHead
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShaft
import at.orchaldir.gm.core.model.item.equipment.style.SpearHead
import at.orchaldir.gm.core.model.item.equipment.style.SpearShape
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage

fun main() {
    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "axes.svg",
        CHARACTER_CONFIG,
        addNames(SpearShape.entries),
        FIXATION,
    ) { distance, fixation, shape ->
        val withSpearHead = PolearmHeadWithSpearHead(
            SpearHead(
                shape,
            ),
            fixation,
        )
        val polearm = Polearm(
            withSpearHead,
            SimpleShaft(
                FillLookupItemPart(Color.SaddleBrown)
            )
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
