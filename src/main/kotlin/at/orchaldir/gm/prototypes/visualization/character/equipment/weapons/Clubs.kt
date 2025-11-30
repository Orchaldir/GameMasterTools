package at.orchaldir.gm.prototypes.visualization.character.equipment.weapons

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.OneHandedClub
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.FixedColor
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.CircularShape.Circle
import at.orchaldir.gm.utils.math.shape.RectangularShape.*
import at.orchaldir.gm.utils.math.shape.RotatedShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape

fun main() {
    val ironId = MaterialId(1)
    val width = Factor.fromPercentage(100)
    val wide = Factor.fromPercentage(120)
    val rotatedShape = RotatedShape(
        listOf(
            Pair(0, 20),
            Pair(20, 40),
            Pair(60, 40),
        )
    )
    val bigSpike = Spike(FULL, THIRD, ColorSchemeItemPart(ironId))
    val smallSpike = Spike(HALF, THIRD, ColorSchemeItemPart(ironId))
    val morningStar = MorningStarHead(CircularArrangement(smallSpike, 9))
    val chain = Chain(main = ColorSchemeItemPart(ironId, FixedColor(Color.Black)))
    val spiked = SpikedMaceHead(spike = smallSpike, 3)
    val rounded = SimpleClubHead(UsingRectangularShape(RoundedRectangle, width))
    val heads = listOf(
        Pair("Baton", NoClubHead),
        Pair("Hammer", SimpleClubHead(UsingRectangularShape(Rectangle, width))),
        Pair("Rounded Hammer", rounded),
        Pair("Rounded Mace", SimpleClubHead(UsingCircularShape(Circle))),
        Pair("Simple Flanged Mace", SimpleFlangedHead(UsingRectangularShape(ReverseTeardrop, wide))),
        Pair("Complex Flanged Mace", ComplexFlangedHead(rotatedShape)),
        Pair("Spiked Mace", spiked),
        Pair("Flail 1", FlailHead(morningStar, chain)),
        Pair("Flail 2", FlailHead(spiked, chain)),
        Pair("Flail 3", FlailHead(rounded, chain)),
        Pair("Morning Star", morningStar),
        Pair("Warhammer", WarhammerHead(spike = bigSpike)),
    ).toMutableList()
    val iron = Material(ironId, color = Color.Gray)
    val gilded = Material(MaterialId(0), color = Color.Gold)

    renderCharacterTableWithoutColorScheme(
        State(Storage(listOf(iron, gilded))),
        "clubs.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        heads,
    ) { distance, head, size ->
        val fixation = if (head is NoClubHead) {
            NoHeadFixation
        } else {
            SocketedHeadHead(part = ColorSchemeItemPart(ironId))
        }
        val polearm = OneHandedClub(
            head,
            size,
            fixation,
            SIMPLE_SHAFT,
        )
        Pair(createAppearance(distance), from(polearm))
    }
}
