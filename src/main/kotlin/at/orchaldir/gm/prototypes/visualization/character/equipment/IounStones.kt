package at.orchaldir.gm.prototypes.visualization.character.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsKeyMap
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val sphere = UsingCircularShape()
    val pyramid = UsingCircularShape(CircularShape.Triangle)
    val diamond = UsingCircularShape(CircularShape.Diamond)
    val ellipse = UsingRectangularShape(RectangularShape.Ellipse)
    val config = listOf(
        Pair("1", listOf(sphere)),
        Pair("2", listOf(sphere, pyramid)),
        Pair("3", listOf(sphere, pyramid, diamond)),
        Pair("4", listOf(sphere, pyramid, diamond, ellipse)),
    )
    renderCharacterTableWithoutColorScheme(
        State(),
        "ioun-stones.svg",
        CHARACTER_CONFIG,
        config,
        addNames(Size.entries),
        true,
    ) { distance, size, stones ->
        val map: MutableMap<BodySlot, EquipmentData> = mutableMapOf()

        stones.withIndex().forEach { (index, stone) ->
            map[BodySlot.getIounStoneSlot(index)] = IounStone(stone, size)
        }

        Pair(createAppearance(distance), fromSlotAsKeyMap(map))
    }
}

private fun createAppearance(distance: Distance) =
    HeadOnly(Head(ears = NormalEars(), eyes = TwoEyes()), distance)