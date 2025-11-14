package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.OneHandedClub
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.CircularShape.Circle
import at.orchaldir.gm.utils.math.shape.CircularShape.Hexagon
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.shape.RectangularShape.Rectangle
import at.orchaldir.gm.utils.math.shape.RectangularShape.ReverseTeardrop
import at.orchaldir.gm.utils.math.shape.RectangularShape.RoundedRectangle
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape

fun main() {
    val width = Factor.fromPercentage(100)
    val wide = Factor.fromPercentage(120)
    val heads = listOf<Pair<String,ClubHead>>(
        Pair("Hammer", SimpleClubHead(UsingRectangularShape(Rectangle, width))),
        Pair("Rounded Hammer", SimpleClubHead(UsingRectangularShape(RoundedRectangle, width))),
        Pair("Rounded Mace", SimpleClubHead(UsingCircularShape(Circle))),
        Pair("Flanged Mace", SimpleFlangedHead(UsingRectangularShape(ReverseTeardrop, wide))),
    ).toMutableList()

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "clubs.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        heads,
    ) { distance, head, size ->
        val polearm = OneHandedClub(
            head,
            size,
            SocketedHeadHead(),
            SIMPLE_SHAFT,
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
