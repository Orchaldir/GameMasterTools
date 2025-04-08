package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape.Square
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape.Teardrop
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Color.Red
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters

fun main() {
    val default = SimpleOrnament()
    val red = SimpleOrnament(color = Red)

    val equipmentTable: List<List<EquipmentMap<EquipmentData>>> = mutableListOf(
        OrnamentShape.entries.map {
            StudEarring(SimpleOrnament(it))
        },
        OrnamentShape.entries.map {
            StudEarring(OrnamentWithBorder(it))
        },
        listOf(
            DangleEarring(default, red, listOf(Medium)),
            DangleEarring(default, red, listOf(Small, Medium, Large)),
            DangleEarring(red, red, listOf(Small, Small, Small)),
            DangleEarring(default, SimpleOrnament(Teardrop, Blue), listOf(Small, Small, Small)),
        ),
        listOf(
            DropEarring(fromPercentage(20), fromPercentage(20)),
            DropEarring(fromPercentage(30), fromPercentage(30), red, red),
            DropEarring(fromPercentage(40), fromPercentage(40), bottom = OrnamentWithBorder(Square, Blue)),
            DropEarring(fromPercentage(60), fromPercentage(40), bottom = SimpleOrnament(Teardrop, Blue)),
        ),
        listOf(
            HoopEarring(fromPercentage(40), Small),
            HoopEarring(fromPercentage(60), Medium),
            HoopEarring(fromPercentage(80), Large),
        ),
    ).map { row ->
        row.map {
            val earring = Earring(it)
            EquipmentMap(earring to setOf(setOf(BodySlot.EarSlotLeft), setOf(BodySlot.EarSlotRight)))
        }
    }

    renderCharacterTable(
        "earrings.svg",
        CHARACTER_CONFIG,
        HeadOnly(Head(ears = NormalEars(), eyes = TwoEyes()), fromMeters(1)),
        equipmentTable,
    )
}
