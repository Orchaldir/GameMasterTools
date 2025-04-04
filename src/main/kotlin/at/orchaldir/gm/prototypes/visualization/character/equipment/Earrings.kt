package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape.Square
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Color.Red
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters

fun main() {
    val dangle = SimpleOrnament(color = Red)

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
            Earring(DropEarring(fromPercentage(20), size = Small)),
            Earring(DropEarring(fromPercentage(40), bottom = SimpleOrnament(color = Blue), size = Medium)),
            Earring(DropEarring(fromPercentage(60), bottom = SimpleOrnament(Square, Blue), size = Large)),
        ),
        listOf(
            Earring(HoopEarring(fromPercentage(40), Small)),
            Earring(HoopEarring(fromPercentage(60), Medium)),
            Earring(HoopEarring(fromPercentage(80), Large)),
        ),
    )

    renderCharacterTable(
        "earrings.svg",
        CHARACTER_CONFIG,
        HeadOnly(Head(ears = NormalEars(), eyes = TwoEyes()), fromMeters(1)),
        equipmentTable,
    )
}
