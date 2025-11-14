package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.OneHandedAxe
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size.Medium
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage

fun main() {
    val heads = listOf<Pair<String,AxeHead>>(
        Pair("Dagger", SingleBitAxeHead(DaggerAxeBlade(Medium)))
    ).toMutableList()
    heads.addAll(BroadAxeShape.entries.map {
        Pair("$it", SingleBitAxeHead(BroadAxeBlade(it, Medium, Medium)))
    })
    heads.addAll(SymmetricAxeShape.entries.map {
        Pair("$it", DoubleBitAxeHead(SymmetricAxeBlade(it, Medium)))
    })

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "axes.svg",
        CHARACTER_CONFIG,
        FIXATION,
        heads,
    ) { distance, head, fixation ->
        val polearm = OneHandedAxe(
            head,
            fixation,
            SIMPLE_SHAFT,
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
