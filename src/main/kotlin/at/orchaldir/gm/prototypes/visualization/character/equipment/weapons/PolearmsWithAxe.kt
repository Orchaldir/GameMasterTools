package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderEquipmentDataTable
import at.orchaldir.gm.prototypes.visualization.mockMaterial
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val table = listOf(
        listOf(
            create(SingleBitAxeHead(DaggerAxeBlade(Small))),
            create(SingleBitAxeHead(DaggerAxeBlade(Medium))),
            create(SingleBitAxeHead(DaggerAxeBlade(Large))),
        ),
    ).toMutableList()

    BroadAxeShape.entries.forEach { shape ->
        Size.entries.forEach { size ->
            table.add(Size.entries.map { length ->
                create(SingleBitAxeHead(BroadAxeBlade(shape, size, length)))
            })
        }
    }

    SymmetricAxeShape.entries.forEach { shape ->
        table.add(Size.entries.map { size ->
            create(DoubleBitAxeHead(SymmetricAxeBlade(shape, size)))
        })
    }

    renderEquipmentDataTable(
        State(Storage(mockMaterial(Color.Gray))),
        "polearms-axe.svg",
        CHARACTER_CONFIG,
        createAppearance(Distance.fromCentimeters(180)),
        table,
    )
}

fun create(axeHead: AxeHead) = Polearm(
    PolearmHeadWithAxeHead(axeHead, Langets()),
    SIMPLE_SHAFT,
)
