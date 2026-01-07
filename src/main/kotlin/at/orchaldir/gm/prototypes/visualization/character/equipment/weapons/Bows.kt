package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.style.BowShape
import at.orchaldir.gm.core.model.item.equipment.style.GripShape
import at.orchaldir.gm.core.model.item.equipment.style.NoBowGrip
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBowGrip
import at.orchaldir.gm.core.model.item.equipment.style.SimpleGrip
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.FULL

fun main() {
    val grip0 = SimpleGrip(GripShape.Oval, FillLookupItemPart(Color.Red))
    val grip1 = SimpleGrip(GripShape.Straight, FillLookupItemPart(Color.Green))
    val grip2 = SimpleGrip(GripShape.Waisted, FillLookupItemPart(Color.Blue))
    val grips = listOf(
        Pair("No Grip", NoBowGrip),
        Pair("Small Grip", SimpleBowGrip(Size.Small, grip0)),
        Pair("Medium Grip", SimpleBowGrip(Size.Medium, grip1)),
        Pair("Large Grip", SimpleBowGrip(Size.Large, grip2)),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "bows.svg",
        CHARACTER_CONFIG,
        addNames(BowShape.entries),
        grips,
    ) { distance, grip, shape ->
        val polearm = Bow(
            shape,
            FULL,
            grip,
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
