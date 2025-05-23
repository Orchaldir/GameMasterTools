package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Earring
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentEntry
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape.*
import at.orchaldir.gm.core.model.util.render.Color.Blue
import at.orchaldir.gm.core.model.util.render.Color.Red
import at.orchaldir.gm.core.model.util.Size.*
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters

fun main() {
    val default = SimpleOrnament()
    val red = SimpleOrnament(Circle, color = Red)
    val topSize = fromPercentage(20)

    val equipmentTable: List<List<EquipmentDataMap>> = mutableListOf(
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
            DropEarring(topSize, fromPercentage(20), fromPercentage(40)),
            DropEarring(topSize, fromPercentage(30), fromPercentage(50), red, red),
            DropEarring(topSize, fromPercentage(40), fromPercentage(60), bottom = OrnamentWithBorder(Square, Blue)),
            DropEarring(topSize, fromPercentage(60), fromPercentage(60), bottom = SimpleOrnament(Teardrop, Blue)),
        ),
        listOf(
            HoopEarring(fromPercentage(40), Small),
            HoopEarring(fromPercentage(60), Medium),
            HoopEarring(fromPercentage(80), Large),
        ),
    ).map { row ->
        row.map {
            val earring = Earring(it)
            val entry = EquipmentEntry<EquipmentData>(earring, setOf(setOf(BodySlot.LeftEar), setOf(BodySlot.RightEar)))

            EquipmentMap(entry)
        }
    }
    renderCharacterTableWithoutColorScheme(
        State(),
        "earrings.svg",
        CHARACTER_CONFIG,
        HeadOnly(Head(ears = NormalEars(), eyes = TwoEyes()), fromMeters(1)),
        equipmentTable,
    )
}
