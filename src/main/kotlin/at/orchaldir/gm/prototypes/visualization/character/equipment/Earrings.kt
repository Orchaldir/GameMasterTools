package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters

fun main() {
    val dangle = SimpleOrnament(color = Color.Red)

    val equipmentTable: MutableList<List<EquipmentData>> = mutableListOf(
        OrnamentShape.entries.map {
            Earring(StudEarring(SimpleOrnament(it)))
        },
        OrnamentShape.entries.map {
            Earring(StudEarring(OrnamentWithBorder(it)))
        },
        listOf(
            Earring(DangleEarring(dangle, listOf(Medium))),
            Earring(DangleEarring(dangle, listOf(Small, Medium, Large))),
            Earring(DangleEarring(dangle, listOf(Small, Small, Small))),
        ),
        listOf(
            Earring(HoopEarring(Factor.fromPercentage(20), Small)),
            Earring(HoopEarring(Factor.fromPercentage(40), Medium)),
            Earring(HoopEarring(Factor.fromPercentage(80), Large)),
        )
    )

    renderCharacterTable(
        "earrings.svg",
        CHARACTER_CONFIG,
        HeadOnly(Head(ears = NormalEars(), eyes = TwoEyes()), fromMeters(1)),
        equipmentTable,
    )
}
