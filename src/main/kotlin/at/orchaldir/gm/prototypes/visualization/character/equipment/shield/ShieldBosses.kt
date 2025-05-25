package at.orchaldir.gm.prototypes.visualization.character.equipment.shield

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.BodyShape
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.from
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.item.equipment.style.NoShieldBoss
import at.orchaldir.gm.core.model.item.equipment.style.ShieldBossWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShieldBoss
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.FixedColor
import at.orchaldir.gm.core.model.util.render.HorizontalStripesLookup
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTableWithoutColorScheme
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.shape.CircularShape.Diamond
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val bosses = listOf(
        Pair("None", NoShieldBoss),
        Pair("Circle", SimpleShieldBoss()),
        Pair("Diamond", SimpleShieldBoss(Diamond)),
        Pair("Border", ShieldBossWithBorder()),
    )

    renderCharacterTableWithoutColorScheme(
        State(Storage(Material(MaterialId(0), color = Color.Gray))),
        "shield-bosses.svg",
        CHARACTER_CONFIG,
        addNames(Size.entries),
        bosses,
    ) { distance, boss, size ->
        val necklace = Shield(
            UsingCircularShape(),
            size,
            SimpleShieldBorder(),
            boss,
            FillLookupItemPart(
                fill = HorizontalStripesLookup(
                    FixedColor(Color.Blue),
                    FixedColor(Color.Yellow),
                )
            )
        )
        Pair(createAppearance(distance), from(necklace))
    }
}

private fun createAppearance(height: Distance) =
    HumanoidBody(
        Body(BodyShape.Muscular),
        Head(),
        height,
    )